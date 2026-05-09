package com.proyecto.volticfit.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Reservation;

/**
 * Repositorio para la entidad Reservation.
 * Provee métodos de acceso a datos para las reservas por franja horaria.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Cuenta las reservas activas por fecha y franja horaria.
     * Usado para validar el aforo disponible.
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.reservationDate = :date AND r.timeSlot = :timeSlot AND r.state = true")
    long countActiveByDateAndTimeSlot(@Param("date") LocalDate date, @Param("timeSlot") String timeSlot);

    /**
     * Obtiene todas las reservas activas de una fecha y franja horaria.
     * Usado para listar usuarios registrados en una franja.
     */
    @Query("SELECT r FROM Reservation r WHERE r.reservationDate = :date AND r.timeSlot = :timeSlot AND r.state = true")
    List<Reservation> findActiveByDateAndTimeSlot(@Param("date") LocalDate date, @Param("timeSlot") String timeSlot);

    /**
     * Obtiene todas las reservas de una fecha agrupadas por franja.
     * Usado para el resumen de aforo del día.
     */
    List<Reservation> findByReservationDateAndStateTrue(LocalDate date);

    /**
     * Verifica si un usuario ya tiene reserva en una fecha y franja específica.
     */
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.user.idUser = :userId AND r.reservationDate = :date AND r.timeSlot = :timeSlot AND r.state = true")
    boolean existsActiveByUserAndDateAndTimeSlot(@Param("userId") Long userId, @Param("date") LocalDate date, @Param("timeSlot") String timeSlot);

    /**
     * Obtiene todas las reservas activas de un usuario.
     */
    List<Reservation> findByUserIdUserAndStateTrue(Long userId);
}