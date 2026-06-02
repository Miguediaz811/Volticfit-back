package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.MedicalDocument;

/**
 * Repositorio para la entidad MedicalDocument
 */
@Repository
public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long> {
 
    /**
     * Find all documents for a specific restriction.
     */
    List<MedicalDocument> findByRestrictionIdRestriction(Long restrictionId);
}