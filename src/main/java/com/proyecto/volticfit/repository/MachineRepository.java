package com.proyecto.volticfit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Machine;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
     
    Optional<Machine> findByNameIgnoreCase(String name);
    
    // AGREGA ESTA LÍNEA NUEVA:
    List<Machine> findByTypeAndState(String type, Boolean state);
}