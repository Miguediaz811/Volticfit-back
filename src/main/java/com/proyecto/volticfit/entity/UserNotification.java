package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad relacional que representa la tabla Recibe:
 * qué usuario recibió qué notificación y si la leyó.
 */
@Entity
@Data
@Table(name = "Recibe")
public class UserNotification {

    @EmbeddedId
    private UserNotificationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "id_usuario")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("notificationId")
    @JoinColumn(name = "id_notificacion")
    private NotificationEntity notification;

    @Column(name = "leida", nullable = false)
    private boolean leida = false;
}
