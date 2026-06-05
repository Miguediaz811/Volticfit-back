package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fallas_equipos")
public class EquipmentFailure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long maquinaId; 
    private Long usuarioReporteId; 
    private String descripcion;
    
    // Tarea 1.5: Asignar automáticamente estado inicial
    private String estado = "Pendiente de revisión"; 
    
    // Tarea 1.7: Campos adicionales solo para Administrador
    private String observacionesAdmin;
    private String prioridad; 
    
    private LocalDateTime fechaReporte = LocalDateTime.now();
}