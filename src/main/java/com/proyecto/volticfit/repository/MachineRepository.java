package com.proyecto.volticfit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.Machine;


/**
 * Repositorio encargado de
 * la persistencia de máquinas.
 *
 * @author Miguel
 * @version 1.0
 */
public interface MachineRepository
        extends JpaRepository<Machine, Long> {

    /**
     * Busca una máquina por nombre.
     *
     * @param nombre nombre máquina
     * @return máquina encontrada
     */
    Optional<Machine> findByName(String nombre);
}
