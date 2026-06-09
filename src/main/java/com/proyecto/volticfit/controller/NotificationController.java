package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Notification.NotificationRequestDTO;
import com.proyecto.volticfit.service.NotificationService;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.enums.RoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> create(@RequestBody NotificationRequestDTO request) {
        notificationService.createNotification(request);
        MessageResponseDTO res = new MessageResponseDTO();
        res.setMessage("Notificación enviada a la caché del usuario");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }


    @PostMapping("/all")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> createForAll(@RequestBody NotificationRequestDTO request) {
        notificationService.createNotificationForAll(request);
        MessageResponseDTO res = new MessageResponseDTO();
        res.setMessage("Notificación enviada a todos los usuarios activos");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<?> getMyNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(notificationService.getMyNotifications(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<MessageResponseDTO> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAsRead(id, userId);
        MessageResponseDTO res = new MessageResponseDTO();
        res.setMessage("Marcada como leída");
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> deleteNotification(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.deleteNotification(id, userId);
        MessageResponseDTO res = new MessageResponseDTO();
        res.setMessage("Notificación eliminada permanentemente de la caché");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/broadcast-history")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<?> getBroadcastHistory() {
        return ResponseEntity.ok(notificationService.getBroadcastHistory());
    }
}