package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    // Tarea 1.2: Buscar solo noticias activas ordenadas de la más nueva a la más antigua
    List<News> findByActivoTrueOrderByFechaPublicacionDesc();
}