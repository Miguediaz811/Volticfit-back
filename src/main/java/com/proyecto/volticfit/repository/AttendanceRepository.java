package com.proyecto.volticfit.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Attendance;

/**
 * Repositorio para la entidad Attendance.
 * Provee métodos de acceso a datos para los registros de asistencia.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * Cuenta el total de registros manuales en el sistema.
     * Usado para el control de seguridad del límite de 15 registros manuales.
     */
    long countByIsManualTrue();

    /**
     * Busca el último registro de un usuario en una fecha específica.
     * Usado para validar coherencia (no duplicar ENTRADA sin SALIDA).
     */
    Optional<Attendance> findTopByUserIdUserAndDateOrderByTimeDesc(Long userId, LocalDate date);

    /**
     * Obtiene todos los registros de asistencia de un usuario.
     */
    List<Attendance> findByUserIdUserOrderByDateDescTimeDesc(Long userId);

    /**
     * Obtiene todos los registros de asistencia de una fecha específica.
     */
    List<Attendance> findByDateOrderByTimeAsc(LocalDate date);

    /**
     * Busca registros manuales de un usuario en una fecha.
     * Usado para validar coherencia del historial.
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.idUser = :userId AND a.date = :date ORDER BY a.time DESC")
    List<Attendance> findByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}