package com.proyecto.volticfit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * Entidad que representa un documento médico asociado a una restricción médica.
 */
@Entity
@Data
@Table(name = "DocumentoMedico")
public class MedicalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long idDocument;

    @Column(name = "nombre_archivo")
    private String fileName;

    @Column(name = "ruta_archivo")
    private String filePath;

    @Column(name = "fecha_subida")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_restriccion", nullable = false)
    private MedicalRestriction restriction;
}
