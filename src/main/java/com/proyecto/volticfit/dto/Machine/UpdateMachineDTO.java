package com.proyecto.volticfit.dto.Machine;

import lombok.Data;

/**
 * DTO utilizado para actualizar 
 * los datos de una máquina existente.
 */
@Data
public class UpdateMachineDTO {

    private String name;

    private String type;

    private Boolean state;
}
