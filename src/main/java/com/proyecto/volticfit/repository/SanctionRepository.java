package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Sanction;

@Repository
public interface SanctionRepository extends JpaRepository<Sanction, Long>{

        List<Sanction> findByUsuarioSanctionListUserId(Long userId);

}
