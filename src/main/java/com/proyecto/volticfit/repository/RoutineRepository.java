package com.proyecto.volticfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Routine;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    
}
