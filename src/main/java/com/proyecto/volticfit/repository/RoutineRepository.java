package com.proyecto.volticfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Routine;
/**
 * Repositorio para la entidad Routine, que proporciona métodos para realizar operaciones CRUD en la base de datos relacionadas con las rutinas.
 */
@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    
}
