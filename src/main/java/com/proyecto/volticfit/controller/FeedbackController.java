package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Feedback.FeedbackRequestDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.entity.Feedback;
import com.proyecto.volticfit.service.FeedbackService;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.enums.RoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendFeedback(
            @RequestBody FeedbackRequestDTO dto, 
            HttpServletRequest request) {
            
        Long userId = (Long) request.getAttribute("userId");
        Feedback guardado = feedbackService.registerFeedback(userId, dto);
        
        MessageResponseDTO res = new MessageResponseDTO();
        res.setMessage("Feedback enviado. Código de seguimiento: " + guardado.getCodigoSeguimiento());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<?> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }
}