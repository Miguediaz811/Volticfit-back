package com.proyecto.volticfit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Machine;

/**
 * Repositorio para la entidad Machine, que proporciona métodos para realizar operaciones CRUD en la base de datos relacionadas con las máquinas.
 */
@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
     /**
      * Encuentra una máquina por su nombre, ignorando mayúsculas y minúsculas.
      * @param name Nombre de la máquina a buscar
      * @return Optional que contiene la máquina si se encuentra, o vacío si no se encuentra
      */
        Optional<Machine> findByNameIgnoreCase(String name);
        
     /**
      * HU38: Recupera todas las máquinas que se encuentran activas lógicamente.
      */
        List<Machine> findByStateTrue();
}