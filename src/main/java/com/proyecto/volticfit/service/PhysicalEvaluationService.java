package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.PhysicalEvaluation.PhysicalEvaluationRequestDTO;
import com.proyecto.volticfit.dto.PhysicalEvaluation.PhysicalEvaluationResponseDTO;
import com.proyecto.volticfit.entity.PhysicalEvaluation;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.PhysicalEvaluationRepository;
import com.proyecto.volticfit.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class that handles the core business logic for scheduling, tracking, 
 * and updating status transitions for physical evaluation appointments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PhysicalEvaluationService {

    private final PhysicalEvaluationRepository physicalEvaluationRepository;
    private final UsersRepository usersRepository;

    /**
     * Retrieves all scheduled physical evaluation appointments associated with a student user.
     *
     * @param userId the unique identifier of the student user
     * @return a list of {@link PhysicalEvaluationResponseDTO} containing appointment details
     */
    public List<PhysicalEvaluationResponseDTO> getAppointmentsByStudent(Long userId) {
        log.info("Fetching scheduled physical evaluations for student ID: {}", userId);
        return physicalEvaluationRepository.findByUser_IdUser(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates and persists a new physical evaluation appointment record after validating 
     * both student and instructor infrastructure existences.
     *
     * @param requestDTO the data transfer object containing the requested schedule details
     * @return a {@link PhysicalEvaluationResponseDTO} representing the newly scheduled appointment
     * @throws RuntimeException if either the student or instructor user record is not found
     */
    public PhysicalEvaluationResponseDTO scheduleEvaluation(PhysicalEvaluationRequestDTO requestDTO) {
        log.info("Scheduling new physical evaluation on {} for student ID: {}", requestDTO.getDate(), requestDTO.getUserId());

        Users user = usersRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Student record not found"));

        Users instructor = usersRepository.findById(requestDTO.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor record not found"));

        PhysicalEvaluation evaluation = PhysicalEvaluation.builder()
                .date(requestDTO.getDate())
                .startTime(requestDTO.getStartTime())
                .endTime(requestDTO.getEndTime())
                .notes(requestDTO.getNotes())
                .user(user)
                .instructor(instructor)
                .build();

        return mapToDTO(physicalEvaluationRepository.save(evaluation));
    }

    /**
     * Updates the status attribute of an existing physical evaluation appointment slot.
     *
     * @param id        the unique identifier of the physical evaluation appointment
     * @param newStatus the target status string to apply (e.g., "completada", "cancelada")
     * @return a {@link PhysicalEvaluationResponseDTO} representing the updated appointment state
     * @throws RuntimeException if the appointment matching the provided ID is not found
     */
    public PhysicalEvaluationResponseDTO updateStatus(Long id, String newStatus) {
        log.info("Updating appointment ID: {} status to: {}", id, newStatus);
        PhysicalEvaluation evaluation = physicalEvaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        evaluation.setStatus(newStatus);
        return mapToDTO(physicalEvaluationRepository.save(evaluation));
    }

    /**
     * Transforms an internal structural {@link PhysicalEvaluation} database entity record 
     * into an external response data transfer object layer wrapper, assembling full participant names.
     *
     * @param entity the internal source entity instance to extract values from
     * @return a mapped and configured {@link PhysicalEvaluationResponseDTO} instance
     */
    private PhysicalEvaluationResponseDTO mapToDTO(PhysicalEvaluation entity) {
        String userFullName = entity.getUser().getNames() + " " + entity.getUser().getSurnames();
        String instructorFullName = entity.getInstructor().getNames() + " " + entity.getInstructor().getSurnames();

        return PhysicalEvaluationResponseDTO.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .userId(entity.getUser().getIdUser())
                .userFullName(userFullName)
                .instructorId(entity.getInstructor().getIdUser())
                .instructorFullName(instructorFullName)
                .build();
    }
}