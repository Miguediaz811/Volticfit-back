package com.proyecto.volticfit.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.proyecto.volticfit.entity.IdempotencyRecord;
import com.proyecto.volticfit.enums.IdempotencyStatus;
import com.proyecto.volticfit.repository.IdempotencyRecordRepository;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Filtro de idempotencia que intercepta peticiones mutantes (POST, PUT, PATCH)
 * y garantiza que una misma operación no se ejecute más de una vez.
 *
 * <p>Funcionamiento:</p>
 * <ol>
 *   <li>Si la petición NO trae el header {@code Idempotency-Key}, se deja pasar
 *       normalmente (backward compatible).</li>
 *   <li>Si la clave ya existe con estado {@code COMPLETED}, se retorna la
 *       respuesta almacenada sin ejecutar la lógica del controlador.</li>
 *   <li>Si la clave ya existe con estado {@code PROCESSING}, se retorna
 *       {@code 409 Conflict} para indicar que hay una petición idéntica en curso.</li>
 *   <li>Si la clave ya existe con estado {@code FAILED}, se permite reintentar
 *       (se elimina el registro fallido y se procesa de nuevo).</li>
 *   <li>Si la clave es nueva, se crea un registro {@code PROCESSING}, se ejecuta
 *       la cadena de filtros, se captura la respuesta y se actualiza a
 *       {@code COMPLETED}.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class IdempotencyFilter implements Filter {

    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";
    private static final Set<String> IDEMPOTENT_METHODS = Set.of("POST", "PUT", "PATCH");

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Solo aplica a métodos mutantes
        if (!IDEMPOTENT_METHODS.contains(request.getMethod().toUpperCase())) {
            chain.doFilter(request, response);
            return;
        }

        // Si no viene el header, dejar pasar sin idempotencia (backward compatible)
        String idempotencyKey = request.getHeader(IDEMPOTENCY_HEADER);
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        // Buscar si la clave ya existe
        var existingRecord = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isPresent()) {
            IdempotencyRecord record = existingRecord.get();

            switch (record.getStatus()) {
                case COMPLETED -> {
                    // Retornar la respuesta almacenada sin re-ejecutar
                    log.info("🔁 Idempotencia: clave {} ya procesada, retornando respuesta almacenada", idempotencyKey);
                    replayResponse(response, record);
                    return;
                }
                case PROCESSING -> {
                    // Petición duplicada en curso
                    log.warn("⚠️ Idempotencia: clave {} aún en proceso, retornando 409", idempotencyKey);
                    sendConflict(response, "Una petición con esta clave de idempotencia está siendo procesada. Intente más tarde.");
                    return;
                }
                case FAILED -> {
                    // Permitir reintento eliminando el registro fallido
                    log.info("♻️ Idempotencia: clave {} falló previamente, permitiendo reintento", idempotencyKey);
                    idempotencyRecordRepository.delete(record);
                }
            }
        }

        // Crear registro PROCESSING
        IdempotencyRecord newRecord = new IdempotencyRecord();
        newRecord.setIdempotencyKey(idempotencyKey);
        newRecord.setHttpMethod(request.getMethod());
        newRecord.setRequestPath(request.getRequestURI());
        newRecord.setStatus(IdempotencyStatus.PROCESSING);
        newRecord.setCreatedAt(LocalDateTime.now());

        try {
            newRecord = idempotencyRecordRepository.save(newRecord);
        } catch (Exception e) {
            // Race condition: otra petición con la misma clave llegó al mismo tiempo
            log.warn("⚠️ Idempotencia: clave duplicada detectada por la BD para {}", idempotencyKey);
            sendConflict(response, "Petición duplicada detectada. Intente más tarde.");
            return;
        }

        // Envolver la respuesta para capturar el body
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            // Ejecutar la cadena de filtros (JwtFilter → Controllers)
            chain.doFilter(request, responseWrapper);

            // Capturar la respuesta
            byte[] responseBody = responseWrapper.getContentAsByteArray();
            String bodyString = new String(responseBody, responseWrapper.getCharacterEncoding() != null
                    ? responseWrapper.getCharacterEncoding() : "UTF-8");

            // Actualizar registro a COMPLETED
            newRecord.setHttpStatus(responseWrapper.getStatus());
            newRecord.setResponseBody(bodyString);
            newRecord.setContentType(responseWrapper.getContentType());
            newRecord.setStatus(IdempotencyStatus.COMPLETED);
            idempotencyRecordRepository.save(newRecord);

            // Copiar el contenido cacheado a la respuesta real
            responseWrapper.copyBodyToResponse();

        } catch (Exception e) {
            // Marcar como FAILED para permitir reintentos
            newRecord.setStatus(IdempotencyStatus.FAILED);
            idempotencyRecordRepository.save(newRecord);
            throw e;
        }
    }

    /**
     * Reproduce una respuesta previamente almacenada.
     */
    private void replayResponse(HttpServletResponse response, IdempotencyRecord record) throws IOException {
        response.setStatus(record.getHttpStatus());
        if (record.getContentType() != null) {
            response.setContentType(record.getContentType());
        }
        if (record.getResponseBody() != null && !record.getResponseBody().isEmpty()) {
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(record.getResponseBody());
            writer.flush();
        }
    }

    /**
     * Envía una respuesta 409 Conflict con un mensaje JSON.
     */
    private void sendConflict(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"message\":\"" + message + "\"}");
        writer.flush();
    }
}
