package com.proyecto.volticfit.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.Reservation;

public interface GymReservationRepository
        extends JpaRepository<Reservation, Long> {

    boolean existsByDateAndState(
        LocalDate date,
        Boolean state
        );
}