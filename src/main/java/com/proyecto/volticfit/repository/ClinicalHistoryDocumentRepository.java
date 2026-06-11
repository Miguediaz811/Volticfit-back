package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.ClinicalHistoryDocument;

/**
 * Repositorio para documentos adjuntos a la historia clínica.
 */
@Repository
public interface ClinicalHistoryDocumentRepository extends JpaRepository<ClinicalHistoryDocument, Long> {

    /** Todos los documentos de un registro de historia clínica. */
    List<ClinicalHistoryDocument> findByClinicalHistoryIdHistory(Long historyId);

    /** Eliminar todos los documentos de un registro (en cascada vía BD, pero útil para limpieza de disco). */
    List<ClinicalHistoryDocument> findByClinicalHistoryIdHistoryAndIdDocumentIn(Long historyId, List<Long> ids);
}