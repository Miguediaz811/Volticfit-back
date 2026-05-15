package com.proyecto.volticfit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Rutina;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    List<Rutina> findByCorreoUsuarioAndEstadoTrue(String correoUsuario);
    Optional<Rutina> findByIdRutinaAndEstadoTrue(Long idRutina);
}