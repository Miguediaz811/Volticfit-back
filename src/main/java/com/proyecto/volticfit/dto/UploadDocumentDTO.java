package com.proyecto.volticfit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for medical document upload response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentDTO {
    private String fileName;
    private String filePath;
    private String message;
}