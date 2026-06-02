package com.proyecto.volticfit.dto.MachineMaintenance;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;


/**
 * DTO utilizado para crear un nuevo mantenimiento
 * de una máquina, incluyendo detalles como el tipo,
 * descripción, fecha, hora y responsable.
 *
 * @author Miguel
 * @version 1.0
 */
@Data
public class CreateMaintenanceDTO {

    private Long machineId;

    private String type;

    private String description;

    private LocalDate date;

    private LocalTime time;

    private String responsible;
}
