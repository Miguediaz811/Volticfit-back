package com.proyecto.volticfit.service;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.CreateEvaluationDTO;
import com.proyecto.volticfit.dto.InstructorAvailabilityDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.RescheduleEvaluationDTO;
import com.proyecto.volticfit.entity.PhysicalEvaluation;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.repository.PhysicalEvaluationRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j2;

/**
 * Servicio para gestionar las evaluaciones físicas
 */
@Service
@Log4j2 
@RequiredArgsConstructor
public class PhysicalEvaluationService {
    

    private static final List<LocalTime> SHIFT_START_TIMES = List.of(
        LocalTime.of(8, 0),
        LocalTime.of(9, 0),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        LocalTime.of(14, 0),
        LocalTime.of(15, 0),
        LocalTime.of(16, 0),
        LocalTime.of(17, 0)
    );

    private final PhysicalEvaluationRepository evaluationRepository;
    private final UsersRepository usersRepository;
 
    /**
     * Returns availability of all instructors for a given date.
     *
     * @param date the date to check
     * @return list of instructor availability per shift
     */
    public List<InstructorAvailabilityDTO> getInstructorAvailability(LocalDate date) {
        List<Users> instructors = usersRepository.findByRoleNameAndStateTrue(RoleEnum.ADMIN.getValue());
        List<InstructorAvailabilityDTO> availability = new ArrayList<>();
 
        for (Users instructor : instructors) {
            for (LocalTime startTime : SHIFT_START_TIMES) {
                boolean taken = evaluationRepository
                        .existsByInstructorIdUserAndDateAndStartTimeAndStatusNot(
                                instructor.getIdUser(), date, startTime, "cancelada");
 
                availability.add(new InstructorAvailabilityDTO(
                        instructor.getIdUser(),
                        instructor.getNames() + " " + instructor.getSurnames(),
                        startTime.toString(),
                        startTime.plusHours(1).toString(),
                        !taken));
            }
        }
 
        return availability;
    }
 
    /**
     * Schedules a physical evaluation for a user.
     *
     * @param request  the evaluation data
     * @param userId   the user ID
     * @return success message
     */
    @Transactional
    public MessageResponseDTO scheduleEvaluation(CreateEvaluationDTO request, Long userId) {
        if (request.getDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Selecciona una fecha desde hoy en adelante");
        }

        if (!SHIFT_START_TIMES.contains(request.getStartTime())) {
            throw new RuntimeException("Selecciona un horario valido");
        }
 
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));
 
        Users instructor = usersRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new RuntimeException("No se encontro el instructor"));
 
        if (!RoleEnum.ADMIN.getValue().equalsIgnoreCase(instructor.getRole().getName())) {
            throw new RuntimeException("El usuario seleccionado no puede recibir evaluaciones");
        }
 
        

        // Validate slot availability
        boolean taken = evaluationRepository
                .existsByInstructorIdUserAndDateAndStartTimeAndStatusNot(
                        request.getInstructorId(), request.getDate(),
                        request.getStartTime(), "cancelada");
        if (taken) {
            throw new RuntimeException("El horario seleccionado ya no esta disponible");
        }
 
        PhysicalEvaluation evaluation = new PhysicalEvaluation();
        evaluation.setUser(user);
        evaluation.setInstructor(instructor);
        evaluation.setDate(request.getDate());
        evaluation.setStartTime(request.getStartTime());
        evaluation.setEndTime(request.getStartTime().plusHours(1));
        evaluation.setNotes(request.getNotes());
        evaluation.setStatus("programada");
        evaluationRepository.save(evaluation);
 
        log.info("Physical evaluation scheduled for user: {} with instructor: {}",
                userId, request.getInstructorId());
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Evaluacion agendada correctamente");
        return response;
    }
 
    /**
     * Returns all evaluations (admin only).
     *
     * @return list of all evaluations
     */
    public List<PhysicalEvaluation> getAllEvaluations() {
        return refreshPastEvaluations(evaluationRepository.findAll());
    }

    /**
     *
     * @param userId the user ID
     * @return list of evaluations
     */
    public List<PhysicalEvaluation> getUserEvaluations(Long userId) {
        List<PhysicalEvaluation> evaluations = refreshPastEvaluations(evaluationRepository.findByUserIdUser(userId));
        if (evaluations.isEmpty()) {
            log.info("No evaluations found for user: {}", userId);
        }
        return evaluations;
    }
 
    /**
     * Reschedules a physical evaluation.
     *
     * @param id            the evaluation ID
     * @param request       the new schedule data
     * @param requesterId   the requester ID
     * @param requesterRole the requester role
     * @return success message
     */
    @Transactional
    public MessageResponseDTO rescheduleEvaluation(Long id, RescheduleEvaluationDTO request,
            Long requesterId, String requesterRole) {
        PhysicalEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluacion no encontrada"));

        if (request.getDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Selecciona una fecha desde hoy en adelante");
        }
 
        if (!"admin".equalsIgnoreCase(requesterRole)
                && !evaluation.getUser().getIdUser().equals(requesterId)) {
            throw new RuntimeException("No tienes permiso para reprogramar esta evaluacion");
        }
 
        if ("cancelada".equals(evaluation.getStatus()) || "realizada".equals(evaluation.getStatus())) {
            throw new RuntimeException("Solo se pueden reprogramar evaluaciones pendientes");
        }
 
        if (!SHIFT_START_TIMES.contains(request.getStartTime())) {
            throw new RuntimeException("Selecciona un horario valido");
        }
 
        Users instructor = usersRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new RuntimeException("No se encontro el instructor"));

        if (!RoleEnum.ADMIN.getValue().equalsIgnoreCase(instructor.getRole().getName())) {
            throw new RuntimeException("El usuario seleccionado no puede recibir evaluaciones");
        }
 
        boolean taken = evaluationRepository
                .existsByInstructorIdUserAndDateAndStartTimeAndStatusNot(
                        request.getInstructorId(), request.getDate(),
                        request.getStartTime(), "cancelada");
        if (taken) {
            throw new RuntimeException("El horario seleccionado ya no esta disponible");
        }
 
        evaluation.setDate(request.getDate());
        evaluation.setStartTime(request.getStartTime());
        evaluation.setEndTime(request.getStartTime().plusHours(1));
        evaluation.setInstructor(instructor);
        evaluationRepository.save(evaluation);
 
        log.info("Evaluation {} rescheduled by user: {}", id, requesterId);
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Evaluacion reprogramada correctamente");
        return response;
    }
 
    /**
     * Cancels a physical evaluation.
     *
     * @param id            the evaluation ID
     * @param requesterId   the requester ID
     * @param requesterRole the requester role
     * @return success message
     */
    @Transactional
    public MessageResponseDTO cancelEvaluation(Long id, Long requesterId, String requesterRole) {
        PhysicalEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluacion no encontrada"));
 
        if (!"admin".equalsIgnoreCase(requesterRole)
                && !evaluation.getUser().getIdUser().equals(requesterId)) {
            throw new RuntimeException("No tienes permiso para cancelar esta evaluacion");
        }
 
        if ("realizada".equals(evaluation.getStatus())) {
            throw new RuntimeException("No se puede cancelar una evaluacion ya realizada");
        }

        if ("cancelada".equals(evaluation.getStatus())) {
            throw new RuntimeException("La evaluacion ya esta cancelada");
        }
 
        evaluation.setStatus("cancelada");
        evaluationRepository.save(evaluation);
 
        log.info("Evaluation {} cancelled by user: {}", id, requesterId);
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Evaluacion cancelada correctamente");
        return response;
    }

    @Transactional
    public MessageResponseDTO completeEvaluation(Long id, String requesterRole) {
        if (!"admin".equalsIgnoreCase(requesterRole)) {
            throw new RuntimeException("Solo un administrador puede marcar evaluaciones como realizadas");
        }

        PhysicalEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluacion no encontrada"));

        if ("cancelada".equals(evaluation.getStatus())) {
            throw new RuntimeException("No se puede marcar como realizada una evaluacion cancelada");
        }

        evaluation.setStatus("realizada");
        evaluationRepository.save(evaluation);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Evaluacion marcada como realizada");
        return response;
    }

    @Transactional
    protected List<PhysicalEvaluation> refreshPastEvaluations(List<PhysicalEvaluation> evaluations) {
        LocalDate today = LocalDate.now();
        evaluations.stream()
                .filter(evaluation -> "programada".equalsIgnoreCase(evaluation.getStatus()))
                .filter(evaluation -> evaluation.getDate() != null && evaluation.getDate().isBefore(today))
                .forEach(evaluation -> {
                    evaluation.setStatus("realizada");
                    evaluationRepository.save(evaluation);
                });
        return evaluations;
    }

}
