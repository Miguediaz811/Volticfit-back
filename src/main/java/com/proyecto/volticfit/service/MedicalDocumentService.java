package com.proyecto.volticfit.service;
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
 
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.UploadDocumentDTO;
import com.proyecto.volticfit.entity.MedicalDocument;
import com.proyecto.volticfit.entity.MedicalRestriction;
import com.proyecto.volticfit.repository.MedicalDocumentRepository;
import com.proyecto.volticfit.repository.MedicalRestrictionRepository;
 
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
 
/**
 * Service for managing medical documents.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class MedicalDocumentService {
 
    @Value("${app.upload.path:uploads/medical-docs}")
    private String uploadPath;
 
    private final MedicalDocumentRepository documentRepository;
    private final MedicalRestrictionRepository restrictionRepository;
 
    /**
     * Uploads a medical document for a restriction.
     *
     * @param restrictionId the restriction ID
     * @param file          the file to upload
     * @return upload response with file info
     */
    @Transactional
    public UploadDocumentDTO uploadDocument(Long restrictionId, MultipartFile file) {
        MedicalRestriction restriction = restrictionRepository.findById(restrictionId)
                .orElseThrow(() -> new RuntimeException("Medical restriction not found"));
 
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
 
        String originalName = file.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String uniqueFileName = UUID.randomUUID().toString() + extension;
 
        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
 
            Path filePath = uploadDir.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
 
            MedicalDocument document = new MedicalDocument();
            document.setRestriction(restriction);
            document.setFileName(originalName);
            document.setFilePath(filePath.toString());
            documentRepository.save(document);
 
            log.info("Medical document uploaded for restriction: {}", restrictionId);
            return new UploadDocumentDTO(originalName, filePath.toString(), "Document uploaded successfully");
 
        } catch (IOException e) {
            log.error("Error uploading document: {}", e.getMessage());
            throw new RuntimeException("Error uploading document: " + e.getMessage());
        }
    }
 
    /**
     * Returns all documents for a restriction.
     *
     * @param restrictionId the restriction ID
     * @return list of documents
     */
    public List<MedicalDocument> getDocumentsByRestriction(Long restrictionId) {
        return documentRepository.findByRestrictionIdRestriction(restrictionId);
    }
 
    /**
     * Deletes a medical document.
     *
     * @param id the document ID
     * @return success message
     */
    @Transactional
    public MessageResponseDTO deleteDocument(Long id) {
        MedicalDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
 
        try {
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Could not delete file from disk: {}", e.getMessage());
        }
 
        documentRepository.delete(document);
        log.info("Medical document deleted: {}", id);
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Document deleted successfully");
        return response;
    }
}
