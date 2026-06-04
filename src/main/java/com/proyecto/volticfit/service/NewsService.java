package com.proyecto.volticfit.service;

import com.proyecto.volticfit.entity.News;
import com.proyecto.volticfit.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    // Tarea 1.3: Servicio que trae las últimas noticias (ej. límite de 10)
    public List<News> getLatestActiveNews() {
        return newsRepository.findByActivoTrueOrderByFechaPublicacionDesc()
                .stream()
                .limit(10) // Mostrar máximo 10 noticias en el Home
                .collect(Collectors.toList());
    }
}