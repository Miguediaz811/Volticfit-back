package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.MedicalRestriction;

/**
 * Repositorio para la entidad MedicalRestriction, que proporciona métodos para realizar operaciones CRUD en la base de datos relacionadas con las restricciones médicas.
 */
@Repository
public interface MedicalRestrictionRepository extends JpaRepository<MedicalRestriction, Long> {
    
    /**
     * Find all restrictions for a specific diagnosis.
     *
     * @param diagnosisId the diagnosis ID
     * @return list of restrictions
     */
    List<MedicalRestriction> findByDiagnosisIdDiagnosis(Long diagnosisId);
 
    /**
     * Find all active restrictions for a specific diagnosis.
     *
     * @param diagnosisId the diagnosis ID
     * @param state       the restriction state
     * @return list of active restrictions
     */
    List<MedicalRestriction> findByDiagnosisIdDiagnosisAndState(Long diagnosisId, Boolean state);
}
