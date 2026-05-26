package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Exercise;
import com.proyecto.volticfit.entity.ExerciseId;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, ExerciseId> {
    
    List<Exercise> findByRoutineIdRoutine(Long routineId);
}