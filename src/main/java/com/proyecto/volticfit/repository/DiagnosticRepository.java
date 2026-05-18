package com.proyecto.volticfit.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.Diagnostic;

public interface DiagnosticRepository
        extends JpaRepository<Diagnostic, Integer> {
}