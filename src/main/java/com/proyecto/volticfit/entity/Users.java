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

    @Column(name = "correo", unique = true)
    private String correo;

    @Column(name = "telefono")
    private Long telefono;

    @Column(name = "tipo_doc")
    private String tipoDoc;

    // Mapeo exacto para la 'ñ' de tu script SQL
    @Column(name = "contraseña") 
    private String contrasena;

    @Column(name = "rol")
    private String rol = "aprendiz";

    @Column(name = "estado")
    private Boolean estado = true;
}