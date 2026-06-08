package com.proyecto.volticfit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class ReportAuthorId implements Serializable {

    @Column(name = "id_usuario")
    private Long userId;

    @Column(name = "id_reporte")
    private Integer reportId;
}
