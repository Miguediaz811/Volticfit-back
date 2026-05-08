package com.proyecto.volticfit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.QrCode;

/**
 * Repository for QrCode entity.
 */
@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    /**
     * Find a QR code by its token.
     *
     * @param token the QR token
     * @return an Optional containing the QR code if found
     */
    Optional<QrCode> findByToken(String token);

    /**
     * Find an active (unused) QR code for a specific user.
     *
     * @param userId the user ID
     * @param used   the used status
     * @return an Optional containing the QR code if found
     */
    Optional<QrCode> findByUserIdUserAndUsed(Long userId, Boolean used);
}