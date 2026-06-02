package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Entity representing an administrative or statistical report generated within the gym system.
 */
@Entity
@Table(name = "Reporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Integer id;

    @Column(name = "tipo", length = 100)
    private String type;

    @Column(name = "formato", length = 50)
    private String format;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String content;

    @Column(name = "fecha_generacion")
    private LocalDate generationDate;
}