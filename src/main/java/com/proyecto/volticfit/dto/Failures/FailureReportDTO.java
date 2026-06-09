package com.proyecto.volticfit.dto.Failures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailureReportDTO {
    private String code;
    private Long machineId;
    private String machineName;
    private Long userId;
    private String user;
    private String description;
    private String priority;
    private String status;
    private String createdAt;
}
