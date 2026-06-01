package com.proyecto.volticfit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.ClinicalHistory.CreateClinicalHistoryDTO;
import com.proyecto.volticfit.dto.ClinicalHistory.UpdateClinicalHistoryDTO;
import com.proyecto.volticfit.entity.ClinicalHistory;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.ClinicalHistoryRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service for managing user clinical history.
 * Accessible by both ADMIN and the user themselves.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ClinicalHistoryService {

    // Inyectar el repositorio de historial clínico para manejar las operaciones relacionadas con el historial clínico de los usuarios
    private final ClinicalHistoryRepository clinicalHistoryRepository;

    // Inyectar el repositorio de usuarios para manejar las operaciones relacionadas con los usuarios
    private final UsersRepository usersRepository;

    /**
     * Creates a clinical history entry for a user.
     *
     * @param request       the clinical history data
     * @param requesterId   the ID of the requester
     * @param requesterRole the role of the requester
     * @param targetUserId  the user ID to create the history for (used by admin)
     * @return success message
     */
    @Transactional
    public MessageResponseDTO create(CreateClinicalHistoryDTO request, Long requesterId,
            String requesterRole, Long targetUserId) {
        Long userId = "admin".equalsIgnoreCase(requesterRole) ? targetUserId : requesterId;

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClinicalHistory history = new ClinicalHistory();
        history.setUser(user);
        history.setDescription(request.getDescription());
        history.setDate(request.getDate());
        clinicalHistoryRepository.save(history);

        log.info("Clinical history created for user: {}", userId);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Clinical history entry created successfully");
        return response;
    }

    /**
     * Returns all clinical history entries for a user.
     *
     * @param requesterId   the ID of the requester
     * @param requesterRole the role of the requester
     * @param targetUserId  the user ID to get history for (used by admin)
     * @return list of clinical history entries
     */
    public List<ClinicalHistory> getByUser(Long requesterId, String requesterRole, Long targetUserId) {
        Long userId = "admin".equalsIgnoreCase(requesterRole) ? targetUserId : requesterId;
        List<ClinicalHistory> history = clinicalHistoryRepository.findByUserIdUser(userId);
        if (history.isEmpty()) {
            log.info("No clinical history found for user: {}", userId);
        }
        return history;
    }

    /**
     * Updates a clinical history entry.
     *
     * @param id            the history entry ID
     * @param request       the update data
     * @param requesterId   the ID of the requester
     * @param requesterRole the role of the requester
     * @return success message
     */
    @Transactional
    public MessageResponseDTO update(Long id, UpdateClinicalHistoryDTO request,
            Long requesterId, String requesterRole) {
        ClinicalHistory history = clinicalHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinical history entry not found"));

        if (!"admin".equalsIgnoreCase(requesterRole)
                && !history.getUser().getIdUser().equals(requesterId)) {
            throw new RuntimeException("You do not have permission to update this entry");
        }

        if (request.getDescription() != null) history.setDescription(request.getDescription());
        if (request.getDate() != null) history.setDate(request.getDate());
        clinicalHistoryRepository.save(history);

        log.info("Clinical history updated: {}", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Clinical history entry updated successfully");
        return response;
    }

    /**
     * Deletes a clinical history entry.
     *
     * @param id            the history entry ID
     * @param requesterId   the ID of the requester
     * @param requesterRole the role of the requester
     * @return success message
     */
    @Transactional
    public MessageResponseDTO delete(Long id, Long requesterId, String requesterRole) {
        ClinicalHistory history = clinicalHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinical history entry not found"));

        if (!"admin".equalsIgnoreCase(requesterRole)
                && !history.getUser().getIdUser().equals(requesterId)) {
            throw new RuntimeException("You do not have permission to delete this entry");
        }

        clinicalHistoryRepository.delete(history);
        log.info("Clinical history deleted: {}", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Clinical history entry deleted successfully");
        return response;
    }
}