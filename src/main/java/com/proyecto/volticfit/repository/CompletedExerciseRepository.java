package com.proyecto.volticfit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.CompletedExercise;

/**
 * Repositorio para la entidad CompletedExercise, que proporciona métodos para realizar operaciones CRUD en la base de datos relacionadas con los ejercicios completados.
 */
@Repository
public interface CompletedExerciseRepository extends JpaRepository<CompletedExercise, Long> {
    /**
     * Encuentra los ejercicios completados por un usuario específico para una rutina específica.
     * @param userId ID del usuario
     * @param routineId ID de la rutina
     * @return Lista de ejercicios completados por el usuario para la rutina dada
     */
    List<CompletedExercise> findByUserIdUserAndRoutineIdRoutine(Long userId, Long routineId);

    /**
     * Encuentra un ejercicio completado por un usuario específico para una rutina específica y una máquina específica.
     * @param userId ID del usuario
     * @param routineId ID de la rutina
     * @param machineId ID de la máquina
     * @return Optional que contiene el ejercicio completado si se encuentra, o vacío si no se encuentra
     */
    Optional<CompletedExercise> findByUserIdUserAndRoutineIdRoutineAndMachineIdMachine(
            Long userId, Long routineId, Long machineId);
}
