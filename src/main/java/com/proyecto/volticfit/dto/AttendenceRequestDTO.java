package com.proyecto.volticfit.dto;

import lombok.Data;

@Data

/**
 * DTO para representar la solicitud de asistencia.
 */
public class AttendenceRequestDTO {

    private String names;
    private String docNum;

    public AttendenceRequestDTO(String names, String docNum) {
        this.names = names;
        this.docNum = docNum;
    }

}
