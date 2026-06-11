package com.proyecto.volticfit.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.ClinicalHistory.ClinicalHistoryDocumentDTO;
import com.proyecto.volticfit.entity.ClinicalHistory;
import com.proyecto.volticfit.entity.ClinicalHistoryDocument;
import com.proyecto.volticfit.repository.ClinicalHistoryDocumentRepository;
import com.proyecto.volticfit.repository.ClinicalHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Gestiona la subida, descarga y eliminación de documentos (PDF / Word)
 * asociados a registros de historia clínica.
 *
 * Los archivos se guardan en disco bajo {@code app.upload.clinical-docs} y su
 * ruta se persiste en {@code DocumentoHistorialClinico}.
 * La IA los utiliza como contexto al generar rutinas personalizadas.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClinicalHistoryDocumentService {

    /** Tipos MIME permitidos: PDF y documentos Word (.doc / .docx). */
    private static final List<String> ALLOWED_MIME = List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    /** Tamaño máximo: 10 MB. */
    private static final long MAX_BYTES = 10 * 1024 * 1024L;

    @Value("${app.upload.clinical-docs:uploads/clinical-docs}")
    private String uploadPath;

    private final ClinicalHistoryDocumentRepository documentRepository;
    private final ClinicalHistoryRepository clinicalHistoryRepository;

    // ------------------------------------------------------------------ //
    //  SUBIR                                                               //
    // ------------------------------------------------------------------ //

    /**
     * Sube un documento PDF o Word y lo asocia al registro de historia clínica.
     *
     * @param historyId ID del registro de historia clínica
     * @param file      archivo a subir
     * @return DTO con información del archivo guardado
     */
    @Transactional
    public ClinicalHistoryDocumentDTO upload(Long historyId, MultipartFile file) {

        ClinicalHistory history = clinicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("Registro de historia clínica no encontrado: " + historyId));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío.");
        }

        if (file.getSize() > MAX_BYTES) {
            throw new RuntimeException("El archivo supera el tamaño máximo permitido de 10 MB.");
        }

        String mime = file.getContentType() != null ? file.getContentType() : "";
        if (!ALLOWED_MIME.contains(mime)) {
            throw new RuntimeException("Tipo de archivo no permitido. Solo se aceptan PDF y Word (.doc / .docx).");
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "documento";
        String ext = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String storedName = UUID.randomUUID() + ext;

        try {
            Path dir = Paths.get(uploadPath);
            Files.createDirectories(dir);
            Path dest = dir.resolve(storedName);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            ClinicalHistoryDocument doc = new ClinicalHistoryDocument();
            doc.setClinicalHistory(history);
            doc.setFileName(originalName);
            doc.setFilePath(dest.toString());
            doc.setMimeType(mime);
            doc.setSizeBytes(file.getSize());
            documentRepository.save(doc);

            log.info("Documento clínico subido: historialId={} archivo={}", historyId, originalName);

            return new ClinicalHistoryDocumentDTO(
                    doc.getIdDocument(), originalName, mime, file.getSize(), doc.getUploadedAt(),
                    "Documento subido correctamente.");

        } catch (IOException e) {
            log.error("Error al guardar documento clínico: {}", e.getMessage());
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ //
    //  LISTAR                                                              //
    // ------------------------------------------------------------------ //

    /**
     * Devuelve los metadatos de todos los documentos de un registro de historia
     * clínica.
     *
     * @param historyId ID del registro
     * @return lista de DTOs (sin contenido binario)
     */
    public List<ClinicalHistoryDocumentDTO> list(Long historyId) {
        return documentRepository.findByClinicalHistoryIdHistory(historyId)
                .stream()
                .map(d -> new ClinicalHistoryDocumentDTO(
                        d.getIdDocument(), d.getFileName(), d.getMimeType(),
                        d.getSizeBytes(), d.getUploadedAt(), null))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------ //
    //  DESCARGAR                                                           //
    // ------------------------------------------------------------------ //

    /**
     * Devuelve el recurso de un documento para su descarga.
     *
     * @param historyId  ID del registro de historia clínica
     * @param documentId ID del documento
     * @return recurso descargable
     */
    public Resource download(Long historyId, Long documentId) {
        ClinicalHistoryDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentId));

        if (!doc.getClinicalHistory().getIdHistory().equals(historyId)) {
            throw new RuntimeException("El documento no pertenece a este registro.");
        }

        try {
            Path path = Paths.get(doc.getFilePath());
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("El archivo no se encuentra en el servidor.");
            }
            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo: " + e.getMessage());
        }
    }

    /**
     * Devuelve el tipo MIME de un documento (necesario para el header Content-Type).
     */
    public String getMimeType(Long documentId) {
        return documentRepository.findById(documentId)
                .map(ClinicalHistoryDocument::getMimeType)
                .orElse("application/octet-stream");
    }

    /**
     * Devuelve el nombre original de un documento (necesario para Content-Disposition).
     */
    public String getFileName(Long documentId) {
        return documentRepository.findById(documentId)
                .map(ClinicalHistoryDocument::getFileName)
                .orElse("documento");
    }

    // ------------------------------------------------------------------ //
    //  ELIMINAR                                                            //
    // ------------------------------------------------------------------ //

    /**
     * Elimina un documento tanto del disco como de la base de datos.
     *
     * @param historyId  ID del registro de historia clínica
     * @param documentId ID del documento
     * @return mensaje de confirmación
     */
    @Transactional
    public MessageResponseDTO delete(Long historyId, Long documentId) {
        ClinicalHistoryDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentId));

        if (!doc.getClinicalHistory().getIdHistory().equals(historyId)) {
            throw new RuntimeException("El documento no pertenece a este registro.");
        }

        try {
            Files.deleteIfExists(Paths.get(doc.getFilePath()));
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo del disco: {}", e.getMessage());
        }

        documentRepository.delete(doc);
        log.info("Documento clínico eliminado: id={}", documentId);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Documento eliminado correctamente.");
        return response;
    }
}