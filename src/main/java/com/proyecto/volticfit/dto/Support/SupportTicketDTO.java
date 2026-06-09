package com.proyecto.volticfit.dto.Support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketDTO {
    private String code;
    private Long userId;
    private String user;
    private String subject;
    private String lastMessage;
    private String status;
    private String createdAt;
    private String attachment;
    private String response;
}
