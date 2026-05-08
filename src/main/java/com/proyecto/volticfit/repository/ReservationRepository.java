package com.proyecto.volticfit.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Obtiene reservas activas desde la fecha actual.
     */
    Page<Reservation> findByStateTrueAndReservationDateGreaterThanEqual(
            LocalDate date,
            Pageable pageable
    );
}