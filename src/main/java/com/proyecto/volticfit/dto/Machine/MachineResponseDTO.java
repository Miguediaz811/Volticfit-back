package com.proyecto.volticfit.dto.Machine;

import lombok.Data;

/**
 * DTO utilizado para responder
 * operaciones relacionadas con máquinas.
 */
@Data
public class MachineResponseDTO {

    private String status;

    private String message;
}