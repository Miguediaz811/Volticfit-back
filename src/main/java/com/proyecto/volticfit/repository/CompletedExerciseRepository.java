package com.proyecto.volticfit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.CompletedExercise;

@Repository
public interface CompletedExerciseRepository extends JpaRepository<CompletedExercise, Long> {
    List<CompletedExercise> findByUserIdUserAndRoutineIdRoutine(Long userId, Long routineId);
    Optional<CompletedExercise> findByUserIdUserAndRoutineIdRoutineAndMachineIdMachine(
            Long userId, Long routineId, Long machineId);
}
