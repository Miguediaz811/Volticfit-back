package com.proyecto.volticfit.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.QrValidationResponseDTO;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class QRService {

    private final JwtService jwtService;

    // memoria para evitar reutilización
    private final Set<String> usedTokens = ConcurrentHashMap.newKeySet();
        /**
     * Genera un código QR basado en un token JWT.
     *
     * @param email correo del usuario autenticado
     * @param role rol del usuario
     * @return URL que será convertida en QR
     */
    public String generateQr(String email, String role) {

        String token = jwtService.generateQrToken(email, role);

        return "http://localhost:9090/qr/validate?token=" + token;
    }

        /**
     * Valida un código QR representado como un token JWT.
     *
     * <p>Este método aplica las siguientes reglas de negocio:</p>
     * <ul>
     *     <li>El token no debe haber sido utilizado previamente</li>
     *     <li>El token debe ser válido criptográficamente</li>
     *     <li>El token debe corresponder a un tipo QR</li>
     * </ul>
     *
     * <p>Si el token es válido, se marca como utilizado para evitar reutilización.</p>
     *
     * @param token token JWT obtenido desde el código QR
     * @return {@link QrValidationResponseDTO} con el resultado de la validación
     */
    public QrValidationResponseDTO validateQr(String token) {

        if (usedTokens.contains(token)) {
            return new QrValidationResponseDTO("QR ya usado", false);
        }

        if (!jwtService.isTokenValid(token)) {
            return new QrValidationResponseDTO("QR inválido", false);
        }

        if (!jwtService.isQrToken(token)) {
            return new QrValidationResponseDTO("Token no es QR", false);
        }

        String email = jwtService.extractEmail(token);

        usedTokens.add(token);

        return new QrValidationResponseDTO("Check-in exitoso: " + email, true);
    }

}