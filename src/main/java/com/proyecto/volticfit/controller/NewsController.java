package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home/noticias")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // Tarea 1.4: Endpoint público para entregar las noticias
    @GetMapping
    public ResponseEntity<?> getHomeNews() {
        return ResponseEntity.ok(newsService.getLatestActiveNews());
    }
}