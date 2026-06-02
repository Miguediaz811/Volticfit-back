package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.UploadDocumentDTO;
import com.proyecto.volticfit.entity.MedicalDocument;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.MedicalDocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller for medical document management.
 * Only accessible by ADMIN.
 */
@RestController
@RequestMapping("/api/medical-documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Medical Documents", description = "Medical document upload and management")
public class MedicalDocumentController {

    private final MedicalDocumentService documentService;

    @Operation(summary = "Upload a medical document for a restriction - ADMIN only")
    @PostMapping("/{restrictionId}/upload")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Object> uploadDocument(
            @PathVariable Long restrictionId,
            @RequestParam("file") MultipartFile file) {
        try {
            UploadDocumentDTO response = documentService.uploadDocument(restrictionId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Get all documents for a restriction - ADMIN only")
    @GetMapping("/{restrictionId}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Object> getDocuments(@PathVariable Long restrictionId) {
        try {
            List<MedicalDocument> documents = documentService.getDocumentsByRestriction(restrictionId);
            if (documents.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No documents found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Delete a medical document - ADMIN only")
    @DeleteMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> deleteDocument(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(documentService.deleteDocument(id));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}