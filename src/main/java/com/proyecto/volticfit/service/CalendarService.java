package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Calendar.CalendarCallbackDTO;
import com.proyecto.volticfit.dto.Calendar.CalendarStatusDTO;
import com.proyecto.volticfit.entity.CalendarToken;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.CalendarTokenRepository;
import com.proyecto.volticfit.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service class that handles the core business logic for external calendar integration management
 * and OAuth2 token orchestrations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final CalendarTokenRepository calendarTokenRepository;
    private final UsersRepository usersRepository;
    private final GoogleCalendarService googleCalendarService;

    /**
     * Generates the initialization redirection URL for a specific external calendar provider.
     *
     * @param userId      the unique identifier of the user requesting the connection
     * @param provider    the name of the calendar provider (e.g., "GOOGLE")
     * @param redirectUri the client application callback endpoint URI
     * @return a String containing the external authorization URL
     * @throws IllegalArgumentException if the requested provider is unsupported
     */
    public String getConnectUrl(Long userId, String provider, String redirectUri) {
        if ("GOOGLE".equalsIgnoreCase(provider)) {
            return googleCalendarService.generateAuthorizationUrl(userId, redirectUri);
        }
        throw new IllegalArgumentException("Unsupported calendar provider: " + provider);
    }

    /**
     * Processes the authorization callback data from the provider to exchange authorization codes,
     * extract tokens, and persist them linked to the user account infrastructure.
     *
     * @param callbackDTO the data transfer object containing verification code and context metadata
     * @return a {@link CalendarStatusDTO} representing the newly established synchronization status
     * @throws RuntimeException if the user record matching the incoming ID is not found
     */
    public CalendarStatusDTO processCallback(CalendarCallbackDTO callbackDTO) {
        log.info("Processing integration callback for user ID: {}", callbackDTO.getUserId());
        
        Users user = usersRepository.findById(callbackDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User infrastructure record not found"));

        Map<String, String> tokenData = googleCalendarService.exchangeCodeForTokens(callbackDTO.getCode());

        // Se ajusta la consulta al ID real de tu tabla (user.idUser)
        CalendarToken calendarToken = calendarTokenRepository
                .findByUser_IdUserAndProvider(user.getIdUser(), "GOOGLE")
                .orElse(new CalendarToken());

        calendarToken.setUser(user);
        calendarToken.setAccessToken(tokenData.get("access_token"));
        calendarToken.setRefreshToken(tokenData.get("refresh_token"));
        calendarToken.setProvider("GOOGLE");
        
        long expiresIn = Long.parseLong(tokenData.getOrDefault("expires_in", "3600"));
        calendarToken.setExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));

        calendarTokenRepository.save(calendarToken);

        return CalendarStatusDTO.builder()
                .userId(user.getIdUser())
                .isConnected(true)
                .provider("GOOGLE")
                .expiresAt(calendarToken.getExpiresAt())
                .build();
    }

    /**
     * Evaluates the persistence layers to retrieve token expiration and current connectivity status 
     * for a given user.
     *
     * @param userId the unique identifier of the user
     * @return a {@link CalendarStatusDTO} mapping current synchronization and validity state details
     */
    public CalendarStatusDTO getStatus(Long userId) {
        return calendarTokenRepository.findByUser_IdUser(userId)
                .map(token -> CalendarStatusDTO.builder()
                        .userId(userId)
                        .isConnected(token.getExpiresAt().isAfter(LocalDateTime.now()))
                        .provider(token.getProvider())
                        .expiresAt(token.getExpiresAt())
                        .build())
                .orElse(CalendarStatusDTO.builder()
                        .userId(userId)
                        .isConnected(false)
                        .build());
    }
}