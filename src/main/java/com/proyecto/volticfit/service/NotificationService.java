package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Notification.NotificationRequestDTO;
import com.proyecto.volticfit.entity.NotificationEntity;
import com.proyecto.volticfit.entity.UserNotification;
import com.proyecto.volticfit.entity.UserNotificationId;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.model.Notification;
import com.proyecto.volticfit.repository.NotificationRepository;
import com.proyecto.volticfit.repository.UserNotificationRepository;
import com.proyecto.volticfit.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UsersRepository usersRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;

    /**
     * Crea una notificación para un usuario específico y la persiste en BD.
     */
    @Transactional
    public void createNotification(NotificationRequestDTO dto) {
        NotificationEntity entity = buildEntity(dto);
        notificationRepository.save(entity);

        Users user = usersRepository.findById(dto.getUsuarioDestinoId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        linkToUser(entity, user);
    }

    /**
     * Crea una notificación y la envía a todos los usuarios activos (excepto ADMIN).
     */
    @Transactional
    public void createNotificationForAll(NotificationRequestDTO dto) {
        NotificationEntity entity = buildEntity(dto);
        notificationRepository.save(entity);

        usersRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getState()))
                .filter(u -> u.getRole() == null || !u.getRole().getName().equalsIgnoreCase("ADMIN"))
                .forEach(u -> linkToUser(entity, u));
    }

    /**
     * Devuelve las notificaciones no expiradas de un usuario, ordenadas por fecha desc.
     */
    public List<Notification> getMyNotifications(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return userNotificationRepository.findByUserIdUser(userId).stream()
                .filter(un -> {
                    NotificationEntity n = un.getNotification();
                    return n.getFechaExpiracion() == null || n.getFechaExpiracion().isAfter(now);
                })
                .sorted((a, b) -> b.getNotification().getFechaEnvio().compareTo(a.getNotification().getFechaEnvio()))
                .map(un -> toModel(un.getNotification(), un.isLeida()))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve todas las notificaciones (historial broadcast para admin).
     */
    public List<Notification> getBroadcastHistory() {
        return notificationRepository.findAll().stream()
                .sorted((a, b) -> b.getFechaEnvio().compareTo(a.getFechaEnvio()))
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    /**
     * Marca una notificación como leída para el usuario.
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        UserNotificationId id = new UserNotificationId();
        id.setUserId(userId);
        id.setNotificationId(notificationId);

        userNotificationRepository.findById(id).ifPresent(un -> {
            un.setLeida(true);
            userNotificationRepository.save(un);
        });
    }

    /**
     * Elimina la relación usuario-notificación (el usuario ya no la ve).
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        userNotificationRepository.deleteByNotificationIdAndUserIdUser(notificationId, userId);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private NotificationEntity buildEntity(NotificationRequestDTO dto) {
        NotificationEntity entity = new NotificationEntity();
        entity.setTitulo(dto.getTitulo());
        entity.setMensaje(dto.getMensaje());
        entity.setTipo(dto.getTipo());
        entity.setFechaExpiracion(dto.getFechaExpiracion());
        return entity;
    }

    private void linkToUser(NotificationEntity entity, Users user) {
        UserNotificationId linkId = new UserNotificationId();
        linkId.setUserId(user.getIdUser());
        linkId.setNotificationId(entity.getId());

        UserNotification link = new UserNotification();
        link.setId(linkId);
        link.setUser(user);
        link.setNotification(entity);
        link.setLeida(false);
        userNotificationRepository.save(link);
    }

    private Notification toModel(NotificationEntity e) {
        return toModel(e, false);
    }

    private Notification toModel(NotificationEntity e, boolean leida) {
        Notification n = new Notification();
        n.setId(e.getId());
        n.setTitulo(e.getTitulo());
        n.setMensaje(e.getMensaje());
        n.setTipo(e.getTipo());
        n.setFechaEnvio(e.getFechaEnvio());
        n.setFechaExpiracion(e.getFechaExpiracion());
        n.setLeida(leida);
        return n;
    }
}
