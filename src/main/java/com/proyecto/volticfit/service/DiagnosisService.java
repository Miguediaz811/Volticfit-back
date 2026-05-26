package com.proyecto.volticfit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.CreateDiagnosisDTO;
import com.proyecto.volticfit.dto.CreateMedicalRestrictionDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.UpdateMedicalRestrictionDTO;
import com.proyecto.volticfit.entity.Diagnosis;
import com.proyecto.volticfit.entity.MedicalRestriction;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.DiagnosisRepository;
import com.proyecto.volticfit.repository.MedicalRestrictionRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service for managing user diagnoses and medical restrictions.
 * Only accessible by ADMIN.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final MedicalRestrictionRepository restrictionRepository;
    private final UsersRepository usersRepository;

    /**
     * Creates a new diagnosis for a user.
     * IMC is calculated automatically from weight and height.
     *
     * @param request the diagnosis data
     * @return success message
     */
    @Transactional
    public MessageResponseDTO createDiagnosis(CreateDiagnosisDTO request) {
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        double imc = calculateIMC(request.getWeight(), request.getHeight());

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setUser(user);
        diagnosis.setEvaluator(request.getEvaluator());
        diagnosis.setObservations(request.getObservations());
        diagnosis.setFatPercentage(request.getFatPercentage());
        diagnosis.setMuscleMass(request.getMuscleMass());
        diagnosis.setHeight(request.getHeight());
        diagnosis.setWeight(request.getWeight());
        diagnosis.setImc(imc);
        diagnosis.setGender(request.getGender());
        diagnosis.setAge(request.getAge());
        diagnosis.setDate(request.getDate() != null ? request.getDate() : java.time.LocalDate.now());
        diagnosisRepository.save(diagnosis);

        log.info("Diagnosis created for user: {}", request.getUserId());

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Diagnosis created successfully. IMC: " + String.format("%.2f", imc));
        return response;
    }

    /**
     * Returns all diagnoses for a user.
     *
     * @param userId the user ID
     * @return list of diagnoses
     */
    public List<Diagnosis> getDiagnosesByUser(Long userId) {
        List<Diagnosis> diagnoses = diagnosisRepository.findByUserIdUser(userId);
        if (diagnoses.isEmpty()) {
            log.info("No diagnoses found for user: {}", userId);
        }
        return diagnoses;
    }

    /**
     * Returns a diagnosis by ID.
     *
     * @param id the diagnosis ID
     * @return the diagnosis
     */
    public Diagnosis getDiagnosisById(Long id) {
        return diagnosisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnosis not found"));
    }

    /**
     * Deletes a diagnosis by ID.
     *
     * @param id the diagnosis ID
     * @return success message
     */
    @Transactional
    public MessageResponseDTO deleteDiagnosis(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnosis not found"));
        diagnosisRepository.delete(diagnosis);
        log.info("Diagnosis deleted: {}", id);
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Diagnosis deleted successfully");
        return response;
    }

    /**
     * Creates a medical restriction linked to a diagnosis.
     *
     * @param request the restriction data
     * @return success message
     */
    @Transactional
    public MessageResponseDTO createRestriction(CreateMedicalRestrictionDTO request) {
        Diagnosis diagnosis = diagnosisRepository.findById(request.getDiagnosisId())
                .orElseThrow(() -> new RuntimeException("Diagnosis not found"));

        MedicalRestriction restriction = new MedicalRestriction();
        restriction.setDiagnosis(diagnosis);
        restriction.setDescription(request.getDescription());
        restriction.setType(request.getType());
        restriction.setStartDate(request.getStartDate());
        restriction.setEndDate(request.getEndDate());
        restriction.setState(true);
        restrictionRepository.save(restriction);

        log.info("Medical restriction created for diagnosis: {}", request.getDiagnosisId());

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Medical restriction created successfully");
        return response;
    }

    /**
     * Returns all restrictions for a diagnosis.
     *
     * @param diagnosisId the diagnosis ID
     * @return list of restrictions
     */
    public List<MedicalRestriction> getRestrictionsByDiagnosis(Long diagnosisId) {
        return restrictionRepository.findByDiagnosisIdDiagnosis(diagnosisId);
    }

    /**
     * Updates a medical restriction.
     *
     * @param id      the restriction ID
     * @param request the update data
     * @return success message
     */
    @Transactional
    public MessageResponseDTO updateRestriction(Long id, UpdateMedicalRestrictionDTO request) {
        MedicalRestriction restriction = restrictionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical restriction not found"));

        if (request.getDescription() != null) restriction.setDescription(request.getDescription());
        if (request.getType() != null) restriction.setType(request.getType());
        if (request.getStartDate() != null) restriction.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) restriction.setEndDate(request.getEndDate());
        if (request.getState() != null) restriction.setState(request.getState());
        restrictionRepository.save(restriction);

        log.info("Medical restriction updated: {}", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Medical restriction updated successfully");
        return response;
    }

    /**
     * Deletes a medical restriction.
     *
     * @param id the restriction ID
     * @return success message
     */
    @Transactional
    public MessageResponseDTO deleteRestriction(Long id) {
        MedicalRestriction restriction = restrictionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical restriction not found"));
        restrictionRepository.delete(restriction);
        log.info("Medical restriction deleted: {}", id);
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Medical restriction deleted successfully");
        return response;
    }

    /**
     * Calculates IMC from weight and height.
     *
     * @param weight weight in kg
     * @param height height in meters
     * @return the IMC value
     */
    private double calculateIMC(double weight, double height) {
        return weight / (height * height);
    }
}