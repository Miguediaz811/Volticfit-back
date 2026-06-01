package com.proyecto.volticfit.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.PhysicalEvaluation;

/**
 * Repositorio para la entidad Evaluación Física
 */
@Repository
public interface PhysicalEvaluationRepository extends JpaRepository<PhysicalEvaluation, Long> {
    
 
    /**
     * Find all evaluations for a specific user.
     */
    List<PhysicalEvaluation> findByUserIdUser(Long userId);
 
    /**
     * Find all evaluations for a specific instructor on a date.
     */
    List<PhysicalEvaluation> findByInstructorIdUserAndDate(Long instructorId, LocalDate date);
 
    /**
     * Check if a slot is already taken for an instructor.
     */
    boolean existsByInstructorIdUserAndDateAndStartTimeAndStatusNot( Long instructorId, LocalDate date, LocalTime startTime, String status);
 
    /**
     * Find evaluations by status for a user.
     */
    List<PhysicalEvaluation> findByUserIdUserAndStatus(Long userId, String status);
}
