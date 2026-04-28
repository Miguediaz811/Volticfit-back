package com.proyecto.volticfit.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
/**
 * DTO containing the information needed to send a recovery email.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryEmailDTO {
 
    /** Recipient email address */
    private String to;
 
    /** Subject of the email */
    private String subject;
 
    /** Recovery URL with token */
    private String recoveryUrl;
 
    /** Expiration time in minutes */
    private int expirationMinutes;
}
