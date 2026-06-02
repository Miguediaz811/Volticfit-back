package com.proyecto.volticfit.dto.Machine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO representing the detailed operational and inventory status of a gym machine.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineStatusDTO {

    private Long id;

    private String name;

    private String type;

    private LocalDate registrationDate;

    private Boolean status;
}