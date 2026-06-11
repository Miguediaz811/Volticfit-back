package com.proyecto.volticfit.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Documento (PDF / Word) adjunto a un registro de historia clínica.
 * La IA utiliza estos archivos al momento de generar rutinas personalizadas.
 */
@Entity
@Data
@Table(name = "DocumentoHistorialClinico")
public class ClinicalHistoryDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long idDocument;

    @Column(name = "nombre_archivo", nullable = false)
    private String fileName;

    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String filePath;

    @Column(name = "tipo_mime", nullable = false)
    private String mimeType;

    @Column(name = "tamano_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historial", nullable = false)
    private ClinicalHistory clinicalHistory;
}