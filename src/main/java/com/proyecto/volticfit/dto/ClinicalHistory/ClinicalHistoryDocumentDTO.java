package com.proyecto.volticfit.dto.ClinicalHistory;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un documento adjunto a la historia clínica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalHistoryDocumentDTO {

    private Long idDocument;
    private String fileName;
    private String mimeType;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
    private String message;
}