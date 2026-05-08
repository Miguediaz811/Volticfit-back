package com.proyecto.volticfit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Attendence;

/**
 * Repositorio para la gestión de asistencias.
 *
 * <p>Permite realizar consultas paginadas y filtradas
 * sobre los registros de asistencia.</p>
 */
@Repository
public interface AttendenceRepository extends JpaRepository<Attendence, Long> {

    /**
     * Busca asistencias filtradas por número de documento.
     *
     * @param docNum número de documento del usuario
     * @param pageable configuración de paginación
     * @return página de asistencias
     */
    Page<Attendence> findByDocNum(String docNum, Pageable pageable);
}