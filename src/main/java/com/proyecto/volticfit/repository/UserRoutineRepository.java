package com.proyecto.volticfit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.UserRoutine;
import com.proyecto.volticfit.entity.UserRoutineId;

/**
 * Repositorio para la entidad UserRoutine, que proporciona métodos para realizar operaciones CRUD en la base de datos relacionadas con las rutinas de los usuarios.
 */
@Repository
public interface UserRoutineRepository extends JpaRepository<UserRoutine, UserRoutineId> {
    /**
     * Encuentra las rutinas asociadas a un usuario específico.
     * @param userId ID del usuario
     * @return  Lista de rutinas asociadas al usuario dado
     */
    List<UserRoutine> findByUserIdUser(Long userId);

    /**
     * Encuentra la rutina activa asociada a un usuario específico.
     * @param userId ID del usuario
     * @return  Optional que contiene la rutina activa si se encuentra, o vacío si no se encuentra
     */
    Optional<UserRoutine> findByUserIdUserAndStateTrue(Long userId);

    /**
     * Encuentra las rutinas asociadas a un usuario específico filtrando por su estado activo o inactivo.
     * @param userId ID del usuario
     * @param active Estado de la rutina (true para activas, false para inactivas)
     * @return Lista de rutinas asociadas al usuario dado y con el estado especificado
     */
    List<UserRoutine> findByUserIdUserAndState(Long userId, Boolean active);
}