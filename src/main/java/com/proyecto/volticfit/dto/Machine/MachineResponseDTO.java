package com.proyecto.volticfit.dto.Machine;

import lombok.Data;

/**
 * DTO utilizado para responder
 * operaciones relacionadas con máquinas.
 *
 * @author Miguel
 * @version 1.0
 */
@Data
public class MachineResponseDTO {

    private String status;

    private String message;
}