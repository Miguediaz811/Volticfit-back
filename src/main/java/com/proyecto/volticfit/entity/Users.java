package com.proyecto.volticfit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "correo")
    private String correo;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "contrasena")
    private String contrasena;

    @Column(name = "rol")
    private String rol = "aprendiz";

    @Column(name = "estado")
    private Boolean estado = true;
}
