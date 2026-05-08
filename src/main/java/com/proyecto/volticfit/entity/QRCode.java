package com.proyecto.volticfit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a QR code generated for a user's gym visit.
 */
@Entity
@Data
@Table(name = "QR")
public class QRCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_qr")
    private Long idQr;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(name = "usado")
    private Boolean used = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime createdAt = LocalDateTime.now();
}