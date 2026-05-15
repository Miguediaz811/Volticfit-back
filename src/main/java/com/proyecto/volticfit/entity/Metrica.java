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
@Table(name = "metrica")
public class Metrica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metrica")
    private Long idMetrica;

    @Column(name = "correo_usuario", nullable = false)
    private String correoUsuario;

    @Column(name = "peso_kg", nullable = false)
    private Double pesoKg;

    @Column(name = "estatura_cm", nullable = false)
    private Double estaturaCm;

    // IMC calculado automáticamente: peso(kg) / (estatura(m))^2
    @Column(name = "imc", nullable = false)
    private Double imc;

    @Column(name = "porcentaje_grasa")
    private Double porcentajeGrasa;

    // Medidas corporales en cm
    @Column(name = "cintura_cm")
    private Double cinturaCm;

    @Column(name = "cadera_cm")
    private Double caderaCm;

    @Column(name = "pecho_cm")
    private Double pechoCm;

    @Column(name = "brazo_cm")
    private Double brazoCm;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}