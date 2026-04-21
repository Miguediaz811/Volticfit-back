package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Usuario")
public class Users {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id_usuario;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "tipo_doc")
    private String tipo_doc;

    @Column(name = "num_doc")
    private String num_doc;

    @Column(name = "correo")
    private String correo;

    @Column(name = "telefono")
    private Long telefono;

    @Column(name = "contraseña")
    private String contrasena;

    @Column(name = "rol")
    private String rol = "aprendiz";

    @Column(name = "estado")
    private Boolean estado = true;
}