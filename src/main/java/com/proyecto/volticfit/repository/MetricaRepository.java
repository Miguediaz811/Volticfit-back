package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Metrica;

@Repository
public interface MetricaRepository extends JpaRepository<Metrica, Long> {
    List<Metrica> findByCorreoUsuarioOrderByFechaRegistroDesc(String correoUsuario);
    boolean existsByCorreoUsuario(String correoUsuario);
}