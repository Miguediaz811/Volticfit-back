package com.proyecto.volticfit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.Failures.CreateFailureReportDTO;
import com.proyecto.volticfit.dto.Failures.FailureStatusDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.service.FailureReportService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/failures")
@RequiredArgsConstructor
public class FailureReportController {

    private final FailureReportService failureReportService;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateFailureReportDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            return ResponseEntity.status(HttpStatus.CREATED).body(failureReportService.create(request, userId));
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAll(HttpServletRequest httpRequest) {
        try {
            String role = (String) httpRequest.getAttribute("role");
            return ResponseEntity.ok(failureReportService.getAll(role));
        } catch (RuntimeException e) {
            return error(HttpStatus.FORBIDDEN, e);
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e);
        }
    }

    @PutMapping("/{code}/status")
    public ResponseEntity<Object> updateStatus(
            @PathVariable String code,
            @Valid @RequestBody FailureStatusDTO request,
            HttpServletRequest httpRequest) {
        try {
            String role = (String) httpRequest.getAttribute("role");
            return ResponseEntity.ok(failureReportService.updateStatus(code, request.getStatus(), role));
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e);
        }
    }

    private ResponseEntity<Object> error(HttpStatus status, Exception e) {
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage(e.getMessage());
        return ResponseEntity.status(status).body(response);
    }
}
