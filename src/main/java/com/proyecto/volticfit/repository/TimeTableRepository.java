package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {

    List<TimeTable> findByEstadoTrue();

    List<TimeTable> findByFechaAndEstadoTrue(
            LocalDate fecha
    );
}