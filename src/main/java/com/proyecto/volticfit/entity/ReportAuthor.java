package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad relacional que representa la tabla Realiza_Reporte:
 * qué usuario generó qué reporte.
 */
@Entity
@Data
@Table(name = "Realiza_Reporte")
public class ReportAuthor {

    @EmbeddedId
    private ReportAuthorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "id_usuario")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reportId")
    @JoinColumn(name = "id_reporte")
    private Report report;
}
