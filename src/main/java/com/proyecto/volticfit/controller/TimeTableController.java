package com.proyecto.volticfit.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import com.proyecto.volticfit.entity.TimeTable;
import com.proyecto.volticfit.service.TimeTableService;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class TimeTableController {

    private final TimeTableService timeTableService;

    /*
        Horarios disponibles
     */
    @GetMapping("/disponibles")
    public List<TimeTable> disponibles(
            @RequestParam String rol
    ) {

        timeTableService.validarRol(rol);

        return timeTableService.getDisponibles();
    }

    /*
        Horarios por fecha
     */
    @GetMapping("/fecha")
    public List<TimeTable> porFecha(
            @RequestParam
            @DateTimeFormat(
                    pattern = "yyyy-MM-dd"
            )
            LocalDate fecha
    ) {

        return timeTableService.getByFecha(fecha);
    }

    /*
        Información detallada
     */
    @GetMapping("/{id}")
    public TimeTable detalle(
            @PathVariable Integer id
    ) {

        return timeTableService.getDetalle(id);
    }
}