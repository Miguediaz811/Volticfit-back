package com.proyecto.volticfit.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordResetCodeService {

    /**
     * Verifica si el código de recuperación es válido.
     * Por ahora retorna true para que puedas probar, 
     * luego aquí implementarás la lógica con tu base de datos.
     */
    public boolean isValidCode(String correo, String codigo) {
        return true; 
    }
}