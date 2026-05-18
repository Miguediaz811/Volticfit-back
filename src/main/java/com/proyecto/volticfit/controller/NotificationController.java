package com.proyecto.volticfit.controller;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.Notification.NotificationRequest;
import com.proyecto.volticfit.model.Notification;
import com.proyecto.volticfit.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping("/enviar")
    public Notification enviar(
            @RequestBody NotificationRequest request
    ) {

        return service.send(request);
    }

    @GetMapping
    public List<Notification> listar() {

        return service.getAll();
    }
}