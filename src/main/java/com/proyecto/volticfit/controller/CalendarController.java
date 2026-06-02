package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Calendar.CalendarCallbackDTO;
import com.proyecto.volticfit.dto.Calendar.CalendarConnectRequestDTO;
import com.proyecto.volticfit.dto.Calendar.CalendarStatusDTO;
import com.proyecto.volticfit.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST controller for managing external calendar integrations and OAuth2 synchronization flows.
 */
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CalendarController {

    private final CalendarService calendarService;

    /**
     * Generates the external provider authorization URL to initiate the OAuth2 consent flow.
     *
     * @param request the connection request details containing user ID and provider
     * @return a ResponseEntity containing the generated redirection URL
     */
    @PostMapping("/connect")
    public ResponseEntity<Map<String, String>> getAuthUrl(@RequestBody CalendarConnectRequestDTO request) {
        String url = calendarService.getConnectUrl(request.getUserId(), request.getProvider(), request.getRedirectUri());
        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * Processes the authorization callback code from the external provider to exchange and persist tokens.
     *
     * @param callback the callback data containing the verification code and state
     * @return a ResponseEntity containing the updated connection status details
     */
    @PostMapping("/callback")
    public ResponseEntity<CalendarStatusDTO> handleCallback(@RequestBody CalendarCallbackDTO callback) {
        CalendarStatusDTO status = calendarService.processCallback(callback);
        return ResponseEntity.ok(status);
    }

    /**
     * Retrieves the current calendar synchronization and token validity status for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a ResponseEntity containing the calendar integration status
     */
    @GetMapping("/status/user/{userId}")
    public ResponseEntity<CalendarStatusDTO> getCalendarStatus(@PathVariable Long userId) {
        CalendarStatusDTO status = calendarService.getStatus(userId);
        return ResponseEntity.ok(status);
    }
}