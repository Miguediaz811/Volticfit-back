package com.proyecto.volticfit.dto.Sanctions;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO que representa una sanción junto con los datos del usuario al que pertenece.
 */
@Data
public class SanctionWithUserDTO {

    private Long idSanction;
    private String description;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean state;
    private String clasificacion;

    // Datos del usuario vinculado
    private Long userId;
    private String userNames;
    private String userSurnames;
    private String userDoc;
    private String userEmail;
}