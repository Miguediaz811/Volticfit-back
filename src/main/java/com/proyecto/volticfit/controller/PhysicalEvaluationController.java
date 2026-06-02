package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.PhysicalEvaluation.PhysicalEvaluationRequestDTO;
import com.proyecto.volticfit.dto.PhysicalEvaluation.PhysicalEvaluationResponseDTO;
import com.proyecto.volticfit.service.PhysicalEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for scheduling, tracking, and managing student physical evaluation appointments.
 */
@RestController
@RequestMapping("/api/physical-evaluations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PhysicalEvaluationController {

    private final PhysicalEvaluationService physicalEvaluationService;

    /**
     * Schedules a new physical evaluation appointment between a student and an instructor.
     *
     * @param request the request body details containing dates, times, and participant IDs
     * @return a ResponseEntity containing the created appointment details
     */
    @PostMapping("/schedule")
    public ResponseEntity<PhysicalEvaluationResponseDTO> createAppointment(@RequestBody PhysicalEvaluationRequestDTO request) {
        return ResponseEntity.ok(physicalEvaluationService.scheduleEvaluation(request));
    }

    /**
     * Retrieves the entire appointment history for a specific student using their unique user identifier.
     *
     * @param userId the unique identifier of the student user
     * @return a ResponseEntity containing a list of evaluation records for the specified student
     */
    @GetMapping("/student/{userId}")
    public ResponseEntity<List<PhysicalEvaluationResponseDTO>> getStudentAppointments(@PathVariable Long userId) {
        return ResponseEntity.ok(physicalEvaluationService.getAppointmentsByStudent(userId));
    }

    /**
     * Updates the operational status of a specific physical evaluation appointment.
     *
     * @param id     the unique identifier of the physical evaluation appointment
     * @param status the new status to apply to the appointment (e.g., completed, cancelled)
     * @return a ResponseEntity containing the updated appointment details
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<PhysicalEvaluationResponseDTO> changeStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        return ResponseEntity.ok(physicalEvaluationService.updateStatus(id, status));
    }
}