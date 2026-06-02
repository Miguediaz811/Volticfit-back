package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Entity representing an automated fitness or nutritional recommendation generated for a user based on diagnostics.
 */
@Entity
@Table(name = "Recomendacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacion")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Column(name = "tipo", length = 100)
    private String type;

    @Column(name = "fecha_creacion")
    private LocalDate creationDate;

    @Column(name = "estado")
    private Boolean status;
}