package com.proyecto.volticfit.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Reservation;

/**
 * Repository for Reservation entity.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
     
    /**
     * Count active reservations for a specific shift.
     */
    int countByDateAndStartTimeAndState(LocalDate date, LocalTime startTime, Boolean state);
 
    /**
     * Find all active reservations for a specific shift.
     */
    List<Reservation> findByDateAndStartTimeAndState(LocalDate date, LocalTime startTime, Boolean state);
 
    /**
     * Find a user's active reservation for a specific shift.
     */
    Optional<Reservation> findByDateAndStartTimeAndUserIdUserAndState(
            LocalDate date, LocalTime startTime, Long userId, Boolean state);
 
    /**
     * Find all active reservations for a user.
     */
    List<Reservation> findByUserIdUserAndState(Long userId, Boolean state);
}
