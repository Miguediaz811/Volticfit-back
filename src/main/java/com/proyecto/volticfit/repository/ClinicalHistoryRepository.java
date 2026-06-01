package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.ClinicalHistory;

/**
 * Repositorio para la entidad ClinicalHistory, que proporciona métodos para realizar operaciones CRUD en la base de datos relacionadas con el historial clínico de los usuarios.
 */
@Repository
public interface ClinicalHistoryRepository extends JpaRepository<ClinicalHistory, Long> {
    
    /**
     * Find all clinical history entries for a specific user.
     *
     * @param userId the user ID
     * @return list of clinical history entries
     */
    List<ClinicalHistory> findByUserIdUser(Long userId);
}
