package com.proyecto.volticfit.service;

import com.proyecto.volticfit.entity.TimeTable;
import com.proyecto.volticfit.exception.TimeTableException;
import com.proyecto.volticfit.repository.TimeTableRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TimeTableService {

    private final TimeTableRepository timeTableRepository;

    /*
        Obtener horarios disponibles
     */
    public List<TimeTable> getDisponibles() {

        return timeTableRepository.findByEstadoTrue();
    }

    /*
        Obtener horarios por fecha
     */
    public List<TimeTable> getByFecha(
            LocalDate fecha
    ) {

        return timeTableRepository.findByFechaAndEstadoTrue(
                fecha
        );
    }

    /*
        Obtener detalle horario
     */
    public TimeTable getDetalle(
            Integer id
    ) {

        return timeTableRepository.findById(id)
                .orElseThrow(() ->
                        new TimeTableException(
                                "Horario no encontrado"
                        )
                );
    }

    /*
        Control manual de acceso
     */
    public void validarRol(
            String rol
    ) {

        if (!rol.equalsIgnoreCase("admin")
                && !rol.equalsIgnoreCase("aprendiz")) {

            throw new TimeTableException(
                    "No autorizado"
            );
        }
    }
}