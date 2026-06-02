package com.proyecto.volticfit.dto.MachineMaintenance;

import lombok.Data;

/**
 * DTO de respuesta para operaciones
 * de mantenimiento.
 *
 * @author Miguel
 * @version 1.0
 */
@Data
public class MaintenanceResponseDTO {

    /**
     * Estado de la operación.
     */
    private String status;

    /**
     * Mensaje descriptivo.
     */
    private String message;
}