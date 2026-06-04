package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "noticias")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String resumen;
    private String imagenUrl;
    private boolean activo = true;
    private LocalDateTime fechaPublicacion = LocalDateTime.now();
}