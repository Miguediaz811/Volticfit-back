package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Reservations.CreateReservationDTO;
import com.proyecto.volticfit.dto.Reservations.ShiftResponseDTO;
import com.proyecto.volticfit.entity.Reservation;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.ReservationRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReservationService {
 
    private static final int MAX_SPOTS = 20;
 
    private static final List<LocalTime> SHIFT_START_TIMES = List.of(
            LocalTime.of(8, 0),
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            LocalTime.of(16, 0),
            LocalTime.of(17, 0)
    );
 
    private final ReservationRepository reservationRepository;
    private final UsersRepository usersRepository;
 
    /**
     * Returns all shifts for a given date with their current availability.
     *
     * @param date the date to check
     * @return list of shifts with availability
     */
    public List<ShiftResponseDTO> getAvailableShifts(LocalDate date) {
        return SHIFT_START_TIMES.stream()
                .map(startTime -> {
                    int taken = reservationRepository.countByDateAndStartTimeAndState(date, startTime, true);
                    int available = MAX_SPOTS - taken;
                    return new ShiftResponseDTO(
                            startTime.toString(),
                            startTime.plusHours(1).toString(),
                            available,
                            MAX_SPOTS,
                            available > 0
                    );
                })
                .toList();
    }
 
    /**
     * Creates a reservation for a user on a specific shift.
     *
     * @param request the reservation data
     * @param userId  the user ID
     * @return success message
     */
    @Transactional
    public MessageResponseDTO createReservation(CreateReservationDTO request, Long userId) {
        if (!SHIFT_START_TIMES.contains(request.getStartTime())) {
            throw new RuntimeException("Invalid shift time");
        }
 
        reservationRepository.findByDateAndStartTimeAndUserIdUserAndState(
                request.getDate(), request.getStartTime(), userId, true)
                .ifPresent(r -> {
                    throw new RuntimeException("You already have a reservation for this shift");
                });
 
        int taken = reservationRepository.countByDateAndStartTimeAndState(
                request.getDate(), request.getStartTime(), true);
        if (taken >= MAX_SPOTS) {
            throw new RuntimeException("No spots available for this shift");
        }
 
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
 
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setDate(request.getDate());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getStartTime().plusHours(1));
        reservation.setState(true);
        reservationRepository.save(reservation);
 
        log.info("Reservation created for user: {} on {} at {}", userId, request.getDate(), request.getStartTime());
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Reservation created successfully");
        return response;
    }
 
    /**
     * Cancels a reservation and frees the spot.
     *
     * @param reservationId the reservation ID
     * @param userId        the user ID requesting cancellation
     * @param requesterRole the role of the requester
     * @return success message
     */
    @Transactional
    public MessageResponseDTO cancelReservation(Long reservationId, Long userId, String requesterRole) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
 
        if (!"admin".equalsIgnoreCase(requesterRole) && !reservation.getUser().getIdUser().equals(userId)) {
            throw new RuntimeException("You do not have permission to cancel this reservation");
        }
 
        reservationRepository.delete(reservation);
        log.info("Reservation {} cancelled by user: {}", reservationId, userId);
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Reservation cancelled successfully");
        return response;
    }
 
    /**
     * Returns all active reservations for a user.
     *
     * @param userId the user ID
     * @return list of reservations
     */
    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUserIdUserAndState(userId, true);
    }
}
