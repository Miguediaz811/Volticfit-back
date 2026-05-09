package com.proyecto.volticfit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad usuario representada en el sistema
 */
@Entity
@Data
@Table(name = "Usuario")
public class Users {
<<<<<<< HEAD
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id_usuario")
   private Long id_usuario;

   @Column(name = "nombres")
   private String nombres;
=======
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUser;

    @Column(name = "nombres")
    private String names;
>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f

    @Column(name = "apellidos")
    private String surnames;

<<<<<<< HEAD
=======
    @Column(name = "tipo_doc")
    private String docType;

    @Column(name = "num_doc")
    private String docNum;

>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f
    @Column(name = "correo")
    private String email;

<<<<<<< HEAD
    @Column(name = "contrasena")
    private String contrasena;
=======
    @Column(name = "telefono")
    private Long phone;

    @Column(name = "contraseña")
    private String password;
>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Role role;

    @Column(name = "estado")
<<<<<<< HEAD
    private Boolean estado = true;
}
=======
    private Boolean state = true;
}
>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f
