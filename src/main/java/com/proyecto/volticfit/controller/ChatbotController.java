package com.proyecto.volticfit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.service.GeminiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Controller for the gym chatbot powered by Gemini AI.
 */
@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Tag(name = "Chatbot", description = "Gym chatbot powered by Gemini AI")
public class ChatbotController {

    private final GeminiService geminiService;

    @Data
    static class ChatRequest {
        @NotBlank(message = "Message is required")
        private String message;
    }

    @Data
    static class ChatResponse {
        private String response;
        public ChatResponse(String response) { this.response = response; }
    }

    @Operation(summary = "Send a message to the gym chatbot")
    @PostMapping("/message")
    public ResponseEntity<Object> chat(@RequestBody ChatRequest request) {
        try {
            String response = geminiService.chat(request.getMessage());
            return ResponseEntity.ok(new ChatResponse(response));
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}