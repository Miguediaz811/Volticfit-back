package com.proyecto.volticfit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "fallas_equipos")
public class EquipmentFailure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "maquina_id")
    private Long maquinaId;

    @Column(name = "usuario_reporte_id")
    private Long usuarioReporteId;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado")
    private String estado = "Pendiente de revision";

    @Column(name = "observaciones_admin")
    private String observacionesAdmin;

    @Column(name = "prioridad")
    private String prioridad;

    @Column(name = "fecha_reporte")
    private LocalDateTime fechaReporte = LocalDateTime.now();
}
