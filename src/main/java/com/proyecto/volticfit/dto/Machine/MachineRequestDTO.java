package com.proyecto.volticfit.dto.Machine;

import lombok.Data;

/**
 * DTO utilizado para registrar
 * nuevas máquinas.
 *
 * @author Miguel Angel Nieto 
 * @version 1.0
 */
@Data
public class MachineRequestDTO {

   
    private String name;

    private String type;

    private Boolean state;
}