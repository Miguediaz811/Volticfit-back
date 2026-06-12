package com.proyecto.volticfit.config;

import java.util.stream.Collectors;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.proyecto.volticfit.dto.MessageResponseDTO;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(" "));

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage(message.isBlank() ? "Completa los campos obligatorios." : message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleEntityNotFound(EntityNotFoundException ex) {
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("El registro solicitado no existe en la base de datos.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({ JDBCConnectionException.class, DataAccessResourceFailureException.class, CannotCreateTransactionException.class })
    public ResponseEntity<MessageResponseDTO> handleJdbcConnection(Exception ex) {
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("No se pudo conectar con la base de datos. Verifica que MySQL este encendido y que la configuracion de conexion sea correcta.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<MessageResponseDTO> handleIdempotencyConflict(IdempotencyConflictException ex) {
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleGenericException(Exception ex) {
        MessageResponseDTO response = new MessageResponseDTO();
        String message = translateDatabaseError(ex);
        response.setMessage(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String translateDatabaseError(Exception ex) {
        String errorMsg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        // Traducir errores comunes de JDBC y Hibernate
        if (errorMsg.contains("unknown column") || errorMsg.contains("field list")) {
            return "Error de configuración en la base de datos. Contacte al administrador.";
        }
        if (errorMsg.contains("doesn't exist") || errorMsg.contains("table not found")) {
            return "Error: Tabla de base de datos no encontrada. Contacte al administrador.";
        }
        if (errorMsg.contains("access denied")) {
            return "Error: Acceso denegado a la base de datos.";
        }
        if (errorMsg.contains("unable to acquire jdbc connection") || errorMsg.contains("communications link failure") || errorMsg.contains("driver has not received any packets") || errorMsg.contains("connection refused") || errorMsg.contains("cannot connect")) {
            return "No se pudo conectar con la base de datos. Verifica que MySQL este encendido y que la configuracion de conexion sea correcta.";
        }
        if (errorMsg.contains("duplicate entry")) {
            return "Error: Este registro ya existe en el sistema.";
        }
        if (errorMsg.contains("foreign key")) {
            return "Error: No se puede realizar esta operación debido a relaciones en la base de datos.";
        }
        if (errorMsg.contains("constraint")) {
            return "Error: Violación de restricción de base de datos.";
        }
        if (errorMsg.contains("data too long")) {
            return "Error: Los datos proporcionados son demasiado largos.";
        }
        if (errorMsg.contains("invalid")) {
            return "Error: Datos inválidos para la operación solicitada.";
        }

        // Error genérico
        return "Error interno del servidor. Por favor, intente más tarde.";
    }

    private String formatFieldError(FieldError error) {
        return switch (error.getField()) {
            case "date" -> "Selecciona una fecha valida.";
            case "startTime" -> "Selecciona un horario disponible.";
            case "instructorId" -> "Selecciona un instructor disponible.";
            default -> error.getDefaultMessage() == null ? "Campo invalido." : error.getDefaultMessage();
        };
    }
}
