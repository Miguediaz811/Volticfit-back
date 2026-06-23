package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.config.AppConstants;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de diagnóstico para verificar la conectividad SMTP/Resend en producción.
 * Accesible únicamente por usuarios con rol ADMIN.
 *
 * <p>Ejecuta el envío de forma <strong>síncrona</strong> (sin @Async) para que el
 * resultado — éxito o error detallado — quede reflejado directamente en la respuesta
 * HTTP, facilitando el diagnóstico en Railway sin necesidad de revisar logs.</p>
 */
@RestController
@RequestMapping("/api/admin/email-test")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Email Diagnostics", description = "ADMIN-only endpoint for testing SMTP/Resend connectivity in production")
public class EmailTestController {

    private final EmailService emailService;

    /**
     * Sends a test recovery-code email to the specified address using the same
     * code path as the real password-recovery flow, but synchronously so the
     * HTTP response reflects the actual outcome.
     *
     * @param to destination email address (defaults to the configured sender address)
     * @return 200 with success message, or 500 with the error detail
     */
    @Operation(
        summary = "Send a test email (ADMIN only)",
        description = "Triggers a synchronous SMTP/Resend send to diagnose connectivity issues in production. "
                    + "Uses the same JavaMail path as the real password-recovery flow.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Email sent successfully",
                         content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Email delivery failed — error detail in message",
                         content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
        }
    )
    @PostMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> sendTestEmail(
            @Parameter(description = "Destination address for the test email")
            @RequestParam(defaultValue = "") String to) {

        String destination = (to == null || to.isBlank()) ? AppConstants.MAIL_FROM : to.trim();

        log.info("[EmailTestController] Solicitud de envío de prueba iniciada por ADMIN. Destino: {}", destination);

        String subject = "[VolticFit TEST] Diagnóstico de conectividad SMTP";
        String html = """
                <div style="font-family:Arial,sans-serif;background:#1e1e1e;color:#fff;padding:24px;">
                  <div style="max-width:520px;margin:auto;background:#272727;border:1px solid #f5a800;
                              border-radius:10px;padding:24px;">
                    <h2 style="color:#f5a800;margin:0 0 12px;">Correo de prueba — VolticFit</h2>
                    <p style="color:#d1d5db;">Este mensaje confirma que la conectividad SMTP/Resend
                    está funcionando correctamente desde el entorno de producción.</p>
                    <p style="color:#aaa;font-size:12px;margin-top:16px;">
                      Generado por EmailTestController · Solo visible para administradores.
                    </p>
                  </div>
                </div>
                """;

        try {
            emailService.sendEmailViaJavaMailWithRetry(destination, subject, html);
            String successMsg = "Correo de prueba enviado exitosamente a: " + destination;
            log.info("[EmailTestController] {}", successMsg);
            return ResponseEntity.ok(new MessageResponseDTO(successMsg));
        } catch (Exception e) {
            String errorMsg = "Fallo al enviar correo de prueba a " + destination
                    + ". Error: " + e.getClass().getSimpleName() + " — " + e.getMessage();
            log.error("[EmailTestController] {}", errorMsg, e);
            return ResponseEntity.internalServerError().body(new MessageResponseDTO(errorMsg));
        }
    }
}
