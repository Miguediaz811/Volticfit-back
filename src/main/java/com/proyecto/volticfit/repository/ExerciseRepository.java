package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Exercise;
import com.proyecto.volticfit.entity.ExerciseId;

/**
 * Repositorio para la entidad Exercise, que proporciona métodos para realizar operaciones CRUD en la base de datos relacionadas con los ejercicios.
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, ExerciseId> {
    /***
     * Encuentra los ejercicios asociados a una rutina específica.
     * @param routineId ID de la rutina
     * @return  Lista de ejercicios asociados a la rutina dada
     */
    List<Exercise> findByRoutineIdRoutine(Long routineId);
}