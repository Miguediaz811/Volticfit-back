package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Support.CreateSupportTicketDTO;
import com.proyecto.volticfit.dto.Support.SupportReplyDTO;
import com.proyecto.volticfit.dto.Support.SupportStatusDTO;
import com.proyecto.volticfit.dto.Support.SupportTicketDTO;
import com.proyecto.volticfit.service.SupportTicketService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/support/instructor")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateSupportTicketDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            return ResponseEntity.status(HttpStatus.CREATED).body(supportTicketService.create(request, userId));
        } catch (Exception e) {
            return badRequest(e);
        }
    }


    @GetMapping("/my")
    public ResponseEntity<Object> getMyTickets(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<SupportTicketDTO> tickets = supportTicketService.getMyTickets(userId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAll(HttpServletRequest httpRequest) {
        try {
            String role = (String) httpRequest.getAttribute("role");
            List<SupportTicketDTO> tickets = supportTicketService.getAll(role);
            return ResponseEntity.ok(tickets);
        } catch (RuntimeException e) {
            return forbidden(e);
        } catch (Exception e) {
            return badRequest(e);
        }
    }

    @PutMapping("/{code}/status")
    public ResponseEntity<MessageResponseDTO> updateStatus(
            @PathVariable String code,
            @Valid @RequestBody SupportStatusDTO request,
            HttpServletRequest httpRequest) {
        try {
            String role = (String) httpRequest.getAttribute("role");
            return ResponseEntity.ok(supportTicketService.updateStatus(code, request.getStatus(), role));
        } catch (RuntimeException e) {
            return message(HttpStatus.BAD_REQUEST, e);
        }
    }

    @PostMapping("/{code}/reply")
    public ResponseEntity<MessageResponseDTO> reply(
            @PathVariable String code,
            @Valid @RequestBody SupportReplyDTO request,
            HttpServletRequest httpRequest) {
        try {
            String role = (String) httpRequest.getAttribute("role");
            Long userId = (Long) httpRequest.getAttribute("userId");
            return ResponseEntity.ok(supportTicketService.reply(code, request.getMessage(), role, userId));
        } catch (RuntimeException e) {
            return message(HttpStatus.BAD_REQUEST, e);
        }
    }

    private ResponseEntity<Object> badRequest(Exception e) {
        MessageResponseDTO error = new MessageResponseDTO();
        error.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ResponseEntity<Object> forbidden(Exception e) {
        MessageResponseDTO error = new MessageResponseDTO();
        error.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    private ResponseEntity<MessageResponseDTO> message(HttpStatus status, Exception e) {
        MessageResponseDTO error = new MessageResponseDTO();
        error.setMessage(e.getMessage());
        return ResponseEntity.status(status).body(error);
    }
}