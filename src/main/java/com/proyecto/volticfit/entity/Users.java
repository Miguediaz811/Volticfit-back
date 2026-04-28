package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad usuario representada en el sistema
 */
@Entity
@Data
@Table(name = "Usuario")
public class Users {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUser;

    @Column(name = "nombres")
    private String names;

    @Column(name = "apellidos")
    private String surnames;

    @Column(name = "tipo_doc")
    private String docType;

    @Column(name = "num_doc")
    private String docNum;

    @Column(name = "correo")
    private String email;

    @Column(name = "telefono")
    private Long phone;

    @Column(name = "contraseña")
    private String password;

    @Column(name = "rol")
    private String role = "aprendiz";

    @Column(name = "estado")
    private Boolean state = true;
}