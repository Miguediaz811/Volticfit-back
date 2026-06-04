package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.SupportTicket.SupportTicketDTO;
import com.proyecto.volticfit.entity.SupportTicket;
import com.proyecto.volticfit.service.SupportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService service;

    @PostMapping("/init")
    public ResponseEntity<MessageResponseDTO> iniciarConsulta(@RequestBody SupportTicketDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        SupportTicket ticket = service.startConsulta(userId, dto);
        MessageResponseDTO res = new MessageResponseDTO();
        
        if ("ESCALADA".equals(ticket.getEstado())) {
            res.setMessage("No pude resolver tu consulta. Ticket pasado a instructor (ID: " + ticket.getId() + ")");
        } else {
            res.setMessage("Respuesta del bot para ticket: " + ticket.getId());
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{id}/escalate")
    public ResponseEntity<MessageResponseDTO> escalarManual(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        service.escalateToInstructor(id);
        MessageResponseDTO res = new MessageResponseDTO();
        res.setMessage("Consulta enviada a instructor exitosamente.");
        return ResponseEntity.ok(res);
    }
}