package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Exercise.CompleteExerciseDTO;
import com.proyecto.volticfit.dto.Exercise.ExerciseResponseDTO;
import com.proyecto.volticfit.dto.Routine.RoutineResponseDTO;
import com.proyecto.volticfit.entity.CompletedExercise;
import com.proyecto.volticfit.entity.Diagnosis;
import com.proyecto.volticfit.entity.Exercise;
import com.proyecto.volticfit.entity.ExerciseId;
import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.entity.MedicalRestriction;
import com.proyecto.volticfit.entity.Routine;
import com.proyecto.volticfit.entity.UserRoutine;
import com.proyecto.volticfit.entity.UserRoutineId;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.CompletedExerciseRepository;
import com.proyecto.volticfit.repository.DiagnosisRepository;
import com.proyecto.volticfit.repository.ExerciseRepository;
import com.proyecto.volticfit.repository.MachineRepository;
import com.proyecto.volticfit.repository.MedicalRestrictionRepository;
import com.proyecto.volticfit.repository.RoutineRepository;
import com.proyecto.volticfit.repository.UserRoutineRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service for managing AI-generated personalized routines.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class RoutineService {

    // Período mínimo de 28 días entre la generación de nuevas rutinas para un usuario, definido como una constante
    private static final int MIN_DAYS_BETWEEN_ROUTINES = 28;

    // Repositorio para manejar las rutinas, utilizado para guardar las rutinas generadas por Gemini y mostrar los detalles de la rutina activa y el historial de rutinas de cada usuario
    private final RoutineRepository routineRepository;

    // Repositorio para manejar los ejercicios asociados a las rutinas, utilizado para mostrar los detalles de la rutina activa y el historial de rutinas de cada usuario
    private final ExerciseRepository exerciseRepository;

    // Repositorio para manejar la asignación de rutinas a los usuarios, utilizado para controlar la rutina activa y el historial de rutinas de cada usuario
    private final UserRoutineRepository userRoutineRepository;

    // Repositorio para manejar los ejercicios completados por los usuarios, utilizado para mostrar el progreso en la rutina activa
    private final CompletedExerciseRepository completedExerciseRepository;

    // Repositorio para manejar los diagnósticos asociados a los usuarios, utilizado para generar rutinas personalizadas basadas en el estado físico del usuario
    private final DiagnosisRepository diagnosisRepository;

    // Repositorio para manejar las restricciones médicas asociadas a los diagnósticos de los usuarios
    private final MedicalRestrictionRepository restrictionRepository;

    // Servicio para comunicarse con Gemini AI y generar rutinas personalizadas
    private final MachineRepository machineRepository;

    // Repositorio de usuarios para manejar las operaciones relacionadas con los usuarios
    private final UsersRepository usersRepository;

    // Servicio para comunicarse con Gemini AI y generar rutinas personalizadas
    private final GeminiService geminiService;

    // ObjectMapper para parsear las respuestas JSON de Gemini
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generates a new personalized routine for a user using Gemini AI.
     * Enforces 28-day minimum period between new routines.
     *
     * @param userId the user ID
     * @return the generated routine response
     */
    @Transactional
    public RoutineResponseDTO generateRoutine(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check 28-day minimum period
        Optional<UserRoutine> activeRoutine = userRoutineRepository.findByUserIdUserAndActiveTrue(userId);
        if (activeRoutine.isPresent()) {
            LocalDate lastAssignment = activeRoutine.get().getAssignmentDate();
            long daysSince = ChronoUnit.DAYS.between(lastAssignment, LocalDate.now());
            if (daysSince < MIN_DAYS_BETWEEN_ROUTINES) {
                long daysRemaining = MIN_DAYS_BETWEEN_ROUTINES - daysSince;
                throw new RuntimeException(
                        "You can generate a new routine in " + daysRemaining + " days.");
            }
        }

        // Check if user has diagnosis
        List<Diagnosis> diagnoses = diagnosisRepository.findByUserIdUser(userId);
        boolean hasDiagnosis = !diagnoses.isEmpty();

        String geminiResponse;
        boolean isPersonalized;
        String warningMessage = null;

        if (hasDiagnosis) {
            String userContext = buildUserContext(user, diagnoses);
            geminiResponse = geminiService.generateRoutine(userContext);
            isPersonalized = true;
        } else {
            geminiResponse = geminiService.generateGenericRoutine();
            isPersonalized = false;
            warningMessage = "This routine is not fully adapted to you. " +
                    "We recommend completing a physical diagnosis with your trainer " +
                    "to get a 100% personalized routine.";
        }

        return parseAndSaveRoutine(geminiResponse, user, isPersonalized, warningMessage, activeRoutine);
    }

    /**
     * Returns the active routine for a user with exercise completion status.
     *
     * @param userId the user ID
     * @return the active routine with exercises
     */
    public RoutineResponseDTO getActiveRoutine(Long userId) {
        UserRoutine userRoutine = userRoutineRepository.findByUserIdUserAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("No active routine found"));

        Routine routine = userRoutine.getRoutine();
        List<Exercise> exercises = exerciseRepository.findByRoutineIdRoutine(routine.getIdRoutine());
        List<CompletedExercise> completed = completedExerciseRepository
                .findByUserIdUserAndRoutineIdRoutine(userId, routine.getIdRoutine());

        List<Long> completedMachineIds = completed.stream()
                .map(c -> c.getMachine().getIdMachine())
                .collect(Collectors.toList());

        List<ExerciseResponseDTO> exerciseDTOs = exercises.stream()
                .map(e -> new ExerciseResponseDTO(
                        e.getName(), e.getSets(), e.getReps(),
                        e.getDescription(), e.getGifUrl(),
                        completedMachineIds.contains(e.getMachine().getIdMachine())))
                .collect(Collectors.toList());

        return new RoutineResponseDTO(
                routine.getIdRoutine(), routine.getObjective(), routine.getDuration(),
                routine.getDescription(), routine.getMuscleGroup(), routine.getLevel(),
                true, null, exerciseDTOs);
    }

    /**
     * Returns all routine history for a user.
     *
     * @param userId the user ID
     * @return list of user routines
     */
    public List<UserRoutine> getRoutineHistory(Long userId) {
        return userRoutineRepository.findByUserIdUser(userId);
    }

    /**
     * Marks a single exercise as completed.
     *
     * @param request the complete exercise request
     * @param userId  the user ID
     * @return success message
     */
    @Transactional
    public MessageResponseDTO completeExercise(CompleteExerciseDTO request, Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Routine routine = routineRepository.findById(request.getRoutineId())
                .orElseThrow(() -> new RuntimeException("Routine not found"));
        Machine machine = machineRepository.findById(request.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        Optional<CompletedExercise> existing = completedExerciseRepository
                .findByUserIdUserAndRoutineIdRoutineAndMachineIdMachine(
                        userId, request.getRoutineId(), request.getMachineId());
        if (existing.isPresent()) {
            throw new RuntimeException("Exercise already marked as completed");
        }

        CompletedExercise completedExercise = new CompletedExercise();
        completedExercise.setUser(user);
        completedExercise.setRoutine(routine);
        completedExercise.setMachine(machine);
        completedExercise.setCompletedAt(LocalDateTime.now());
        completedExerciseRepository.save(completedExercise);

        log.info("Exercise completed by user: {} in routine: {}", userId, request.getRoutineId());

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Exercise marked as completed");
        return response;
    }

    /**
     * Marks all exercises in a routine as completed.
     *
     * @param routineId the routine ID
     * @param userId    the user ID
     * @return success message
     */
    @Transactional
    public MessageResponseDTO completeRoutine(Long routineId, Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Routine not found"));
        List<Exercise> exercises = exerciseRepository.findByRoutineIdRoutine(routineId);

        for (Exercise exercise : exercises) {
            Optional<CompletedExercise> existing = completedExerciseRepository
                    .findByUserIdUserAndRoutineIdRoutineAndMachineIdMachine(
                            userId, routineId, exercise.getMachine().getIdMachine());
            if (existing.isEmpty()) {
                CompletedExercise ce = new CompletedExercise();
                ce.setUser(user);
                ce.setRoutine(routine);
                ce.setMachine(exercise.getMachine());
                ce.setCompletedAt(LocalDateTime.now());
                completedExerciseRepository.save(ce);
            }
        }

        log.info("All exercises completed by user: {} in routine: {}", userId, routineId);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("All exercises marked as completed");
        return response;
    }

    // --- Private helpers ---

    private String buildUserContext(Users user, List<Diagnosis> diagnoses) {
        Diagnosis latest = diagnoses.get(diagnoses.size() - 1);
        List<MedicalRestriction> restrictions = restrictionRepository
                .findByDiagnosisIdDiagnosis(latest.getIdDiagnosis());

        StringBuilder context = new StringBuilder();
        context.append("Name: ").append(user.getNames()).append(" ").append(user.getSurnames()).append("\n");
        context.append("Age: ").append(latest.getAge()).append("\n");
        context.append("Gender: ").append(latest.getGender()).append("\n");
        context.append("Weight: ").append(latest.getWeight()).append(" kg\n");
        context.append("Height: ").append(latest.getHeight()).append(" m\n");
        context.append("IMC: ").append(String.format("%.2f", latest.getImc())).append("\n");
        context.append("Fat percentage: ").append(latest.getFatPercentage()).append("%\n");
        context.append("Muscle mass: ").append(latest.getMuscleMass()).append(" kg\n");
        context.append("Observations: ").append(latest.getObservations()).append("\n");

        if (!restrictions.isEmpty()) {
            context.append("Medical restrictions: ");
            restrictions.forEach(r -> context.append(r.getDescription()).append(", "));
        }

        return context.toString();
    }

    @Transactional
    private RoutineResponseDTO parseAndSaveRoutine(String geminiJson, Users user,
            boolean isPersonalized, String warningMessage,
            Optional<UserRoutine> previousActiveRoutine) {
        try {
            String cleanJson = geminiJson
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            JsonNode root = objectMapper.readTree(cleanJson);

            // Deactivate previous routine
            previousActiveRoutine.ifPresent(prev -> {
                prev.setActive(false);
                userRoutineRepository.save(prev);
            });

            // Create Routine
            Routine routine = new Routine();
            routine.setObjective(root.path("objetivo").asText());
            routine.setDuration(root.path("duracion").asText());
            routine.setDescription(root.path("descripcion").asText());
            routine.setMuscleGroup(root.path("grupo_muscular").asText());
            routine.setLevel(root.path("nivel").asText());
            routineRepository.save(routine);

            // Create exercises
            List<ExerciseResponseDTO> exerciseDTOs = new ArrayList<>();
            for (JsonNode exerciseNode : root.path("ejercicios")) {
                String machineName = exerciseNode.path("maquina").asText();
                Optional<Machine> machine = machineRepository.findByName(machineName);

                if (machine.isEmpty()) {
                    log.warn("Machine not found: {}, skipping exercise", machineName);
                    continue;
                }

                ExerciseId exerciseId = new ExerciseId();
                exerciseId.setIdRoutine(routine.getIdRoutine());
                exerciseId.setIdMachine(machine.get().getIdMachine());

                Exercise exercise = new Exercise();
                exercise.setId(exerciseId);
                exercise.setRoutine(routine);
                exercise.setMachine(machine.get());
                exercise.setName(exerciseNode.path("nombre").asText());
                exercise.setSets(exerciseNode.path("series").asInt());
                exercise.setReps(exerciseNode.path("repeticiones").asInt());
                exercise.setDescription(exerciseNode.path("descripcion").asText());
                exerciseRepository.save(exercise);

                exerciseDTOs.add(new ExerciseResponseDTO(
                        exercise.getName(), exercise.getSets(), exercise.getReps(),
                        exercise.getDescription(), null, false));
            }

            // Assign to user
            UserRoutineId urId = new UserRoutineId();
            urId.setIdUser(user.getIdUser());
            urId.setIdRoutine(routine.getIdRoutine());

            UserRoutine userRoutine = new UserRoutine();
            userRoutine.setId(urId);
            userRoutine.setUser(user);
            userRoutine.setRoutine(routine);
            userRoutine.setAssignmentDate(LocalDate.now());
            userRoutine.setState(true);
            userRoutine.setActive(true);
            userRoutineRepository.save(userRoutine);

            log.info("Routine generated and saved for user: {}", user.getIdUser());

            return new RoutineResponseDTO(routine.getIdRoutine(), routine.getObjective(),
                    routine.getDuration(), routine.getDescription(), routine.getMuscleGroup(),
                    routine.getLevel(), isPersonalized, warningMessage, exerciseDTOs);

        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage());
            throw new RuntimeException("Error processing routine from AI: " + e.getMessage());
        }
    }
}