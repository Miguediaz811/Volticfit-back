package com.proyecto.volticfit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "rutina_version")
public class RutinaVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_version")
    private Long idVersion;

    // Referencia a la rutina original
    @Column(name = "id_rutina", nullable = false)
    private Long idRutina;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    // Snapshot de los datos de la rutina en ese momento
    @Column(name = "nombre_rutina", nullable = false)
    private String nombreRutina;

    @Column(name = "descripcion_rutina")
    private String descripcionRutina;

    // Snapshot de ejercicios serializado en JSON
    // Guardamos el detalle completo (ejercicios, series, reps) como texto JSON
    @Column(name = "ejercicios_json", columnDefinition = "TEXT", nullable = false)
    private String ejerciciosJson;

    @Column(name = "fecha_guardado", nullable = false)
    private LocalDateTime fechaGuardado;

    @Column(name = "guardado_por", nullable = false)
    private String guardadoPor;

    // Indica si esta versión fue creada por una restauración
    @Column(name = "es_restauracion", nullable = false)
    private Boolean esRestauracion = false;

    // Si es restauración, de qué versión viene
    @Column(name = "version_origen_restauracion")
    private Integer versionOrigenRestauracion;
}