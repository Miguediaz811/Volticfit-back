package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Reservations.CreateFullGymReservationDTO;
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
            throw new RuntimeException("Selecciona un horario valido");
        }
 
        reservationRepository.findByDateAndStartTimeAndUserIdUserAndState(
                request.getDate(), request.getStartTime(), userId, true)
                .ifPresent(r -> {
                    throw new RuntimeException("Ya tienes una reserva para este horario");
                });
 
        int taken = reservationRepository.countByDateAndStartTimeAndState(
                request.getDate(), request.getStartTime(), true);
        if (taken >= MAX_SPOTS) {
            throw new RuntimeException("No hay cupos disponibles para este horario");
        }
 
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));
 
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setDate(request.getDate());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getStartTime().plusHours(1));
        reservation.setState(true);
        reservationRepository.save(reservation);
 
        log.info("Reservation created for user: {} on {} at {}", userId, request.getDate(), request.getStartTime());
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Reserva creada correctamente");
        return response;
    }

    @Transactional
    public MessageResponseDTO createFullGymReservation(
        CreateFullGymReservationDTO request,
        Long userId) {

    MessageResponseDTO response =
            new MessageResponseDTO();

    try {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        /*
         * Solo funcionarios.
         */
        if (!"FUNCIONARIO".equalsIgnoreCase(
                user.getRole().getName())) {

            response.setMessage(
                    "Only staff members can reserve the entire gym");

            return response;
        }

        /*
         * Validar si ya existe una reserva completa.
         */
        boolean exists =
                reservationRepository
                        .existsByDateAndReservationTypeAndState(
                                request.getReservationDate(),
                                "FULL_GYM",
                                true);

        if (exists) {

            response.setMessage(
                    "The gym is already reserved");

            return response;
        }

        Reservation reservation =
                new Reservation();

        reservation.setUser(user);
        reservation.setDate(
                request.getReservationDate());

        reservation.setStartTime(
                request.getStartTime());

        reservation.setEndTime(
                request.getEndTime());

        reservation.setState(true);

        reservation.setReservationType(
                "FULL_GYM");

        reservationRepository.save(
                reservation);

        response.setMessage(
                "Gym reserved successfully");

        log.info(
                "Full gym reserved by user {}",
                userId);

    } catch (Exception e) {

        log.error(
                "Error creating gym reservation: {}",
                e.getMessage());

        response.setMessage(
                "Error creating reservation");
    }

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
                .orElseThrow(() -> new RuntimeException("No se encontro la reserva"));
 
        if (!"admin".equalsIgnoreCase(requesterRole) && !reservation.getUser().getIdUser().equals(userId)) {
            throw new RuntimeException("No tienes permiso para cancelar esta reserva");
        }
 
        reservationRepository.delete(reservation);
        log.info("Reservation {} cancelled by user: {}", reservationId, userId);
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Reserva cancelada correctamente");
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

    /**
     * Returns all active reservations for administrators.
     *
     * @param requesterRole the role of the requester
     * @return list of active reservations
     */
    public List<Reservation> getAllActiveReservations(String requesterRole) {
        if (!"admin".equalsIgnoreCase(requesterRole)) {
            throw new RuntimeException("No tienes permiso para ver todas las reservas");
        }

        return reservationRepository.findByState(true);
    }
}
