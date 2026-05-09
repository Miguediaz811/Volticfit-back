package com.proyecto.volticfit.dto.Attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO {
 
    private String status;        // "VALID", "INVALID_SANCTION", "EXIT_REGISTERED"

    private String message;

    private String names;

    private String lastNames;

    private String phone;

    private String docType;

    private String docNumber;
 
    // Only populated when status = INVALID_SANCTION
    private String sanctionStartDate;

    private String sanctionEndDate;
    
    private String sanctionDescription;
    
}
