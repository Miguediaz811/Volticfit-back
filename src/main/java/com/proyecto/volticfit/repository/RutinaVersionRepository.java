package com.proyecto.volticfit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.RutinaVersion;

@Repository
public interface RutinaVersionRepository extends JpaRepository<RutinaVersion, Long> {
    List<RutinaVersion> findByIdRutinaOrderByNumeroVersionDesc(Long idRutina);
    Optional<RutinaVersion> findByIdRutinaAndNumeroVersion(Long idRutina, Integer numeroVersion);
    Integer countByIdRutina(Long idRutina);
}