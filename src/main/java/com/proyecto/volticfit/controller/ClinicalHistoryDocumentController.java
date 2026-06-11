package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.ClinicalHistory.ClinicalHistoryDocumentDTO;
import com.proyecto.volticfit.service.ClinicalHistoryDocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints para subir, listar, descargar y eliminar documentos (PDF / Word)
 * asociados a un registro de historia clínica.
 *
 * Base URL: /api/clinical-history/{historyId}/documents
 *
 * Acceso:
 *   - ADMIN: acceso completo a cualquier registro.
 *   - APRENDIZ / FUNCIONARIO: solo a sus propios registros (validado en el service
 *     a través del userId del JWT que pasa el filtro de seguridad).
 */
@RestController
@RequestMapping("/api/clinical-history/{historyId}/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.frontend.url:http://localhost:4200}")
@Tag(name = "Clinical History Documents",
     description = "Subida y gestión de archivos PDF/Word en la historia clínica")
public class ClinicalHistoryDocumentController {

    private final ClinicalHistoryDocumentService documentService;

    // ------------------------------------------------------------------ //
    //  POST /api/clinical-history/{historyId}/documents                   //
    // ------------------------------------------------------------------ //

    @Operation(summary = "Subir un documento PDF o Word a un registro de historia clínica")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> upload(
            @PathVariable Long historyId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            ClinicalHistoryDocumentDTO result = documentService.upload(historyId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ------------------------------------------------------------------ //
    //  GET /api/clinical-history/{historyId}/documents                    //
    // ------------------------------------------------------------------ //

    @Operation(summary = "Listar los documentos de un registro de historia clínica")
    @GetMapping
    public ResponseEntity<Object> list(@PathVariable Long historyId) {
        try {
            List<ClinicalHistoryDocumentDTO> docs = documentService.list(historyId);
            if (docs.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No hay documentos adjuntos a este registro.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ------------------------------------------------------------------ //
    //  GET /api/clinical-history/{historyId}/documents/{documentId}/download
    // ------------------------------------------------------------------ //

    @Operation(summary = "Descargar un documento adjunto a la historia clínica")
    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long historyId,
            @PathVariable Long documentId) {
        try {
            Resource resource = documentService.download(historyId, documentId);
            String mimeType  = documentService.getMimeType(documentId);
            String fileName  = documentService.getFileName(documentId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ------------------------------------------------------------------ //
    //  DELETE /api/clinical-history/{historyId}/documents/{documentId}    //
    // ------------------------------------------------------------------ //

    @Operation(summary = "Eliminar un documento de la historia clínica")
    @DeleteMapping("/{documentId}")
    public ResponseEntity<MessageResponseDTO> delete(
            @PathVariable Long historyId,
            @PathVariable Long documentId) {
        try {
            return ResponseEntity.ok(documentService.delete(historyId, documentId));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}