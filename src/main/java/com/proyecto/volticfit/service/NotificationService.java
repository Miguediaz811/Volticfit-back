package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Notification.NotificationRequestDTO;
import com.proyecto.volticfit.model.Notification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    // CACHÉ EN MEMORIA
    private final Map<Long, Notification> notificationCache = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public void createNotification(NotificationRequestDTO dto) {
        Notification noti = new Notification();
        noti.setId(idGenerator.getAndIncrement());
        noti.setTitulo(dto.getTitulo());
        noti.setMensaje(dto.getMensaje());
        noti.setTipo(dto.getTipo());
        noti.setUsuarioDestinoId(dto.getUsuarioDestinoId());
        noti.setFechaExpiracion(dto.getFechaExpiracion());
        
        notificationCache.put(noti.getId(), noti);
    }

    public List<Notification> getMyNotifications(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return notificationCache.values().stream()
                .filter(n -> n.getUsuarioDestinoId().equals(userId))
                .filter(n -> n.getFechaExpiracion() == null || n.getFechaExpiracion().isAfter(now))
                .sorted((n1, n2) -> n2.getFechaEnvio().compareTo(n1.getFechaEnvio()))
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification noti = notificationCache.get(notificationId);
        if (noti != null && noti.getUsuarioDestinoId().equals(userId)) {
            noti.setLeida(true);
        }
    }

    public void deleteNotification(Long notificationId, Long userId) {
        Notification noti = notificationCache.get(notificationId);
        if (noti != null && noti.getUsuarioDestinoId().equals(userId)) {
            notificationCache.remove(notificationId);
        }
    }
}