package com.proyecto.volticfit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Asistencia")
public class Attendance {
    
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Long idAttendance;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_qr", nullable = true)
    private QrCode qrCode;
 
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime entryTime;
 
    @Column(name = "fecha_salida")
    private LocalDateTime exitTime;
 
    @Column(name = "tipo_registro", nullable = false)
    private String registrationType; // "QR" or "MANUAL"

}
