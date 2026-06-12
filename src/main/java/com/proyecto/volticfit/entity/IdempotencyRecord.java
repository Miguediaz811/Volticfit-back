package com.proyecto.volticfit.entity;

import java.time.LocalDateTime;

import com.proyecto.volticfit.enums.IdempotencyStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que almacena las claves de idempotencia junto con la respuesta HTTP
 * asociada. Permite detectar peticiones duplicadas y retornar la respuesta original
 * sin volver a ejecutar la lógica del controlador.
 */
@Entity
@Table(name = "IdempotencyRecord")
@Data
@NoArgsConstructor
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(nullable = false, length = 10)
    private String httpMethod;

    @Column(nullable = false, length = 500)
    private String requestPath;

    private Integer httpStatus;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column(length = 255)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IdempotencyStatus status = IdempotencyStatus.PROCESSING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
