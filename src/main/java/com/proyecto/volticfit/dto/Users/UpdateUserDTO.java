package com.proyecto.volticfit.dto.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {

    private String names;
 
    private String surnames;
 
    private String docType;
 
    private String docNumber;
 
    @Email(message = "Email must have a valid format")
    private String email;
 
    private String phone;
 
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
