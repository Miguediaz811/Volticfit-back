package com.proyecto.volticfit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Sanction;

/**
 * Repositorio para la entidad Sanction, proporciona métodos para acceder a los datos de sanciones en la base de datos.
 */
@Repository
public interface SanctionRepository extends JpaRepository<Sanction, Long>{        

public interface SanctionRepository extends JpaRepository<Sanction, Long>{

       
}
