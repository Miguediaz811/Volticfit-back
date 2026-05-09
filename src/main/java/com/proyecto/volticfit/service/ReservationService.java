package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Reservation.CreateReservationDTO;
import com.proyecto.volticfit.entity.Reservation;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.ReservationRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio para gestionar las reservas de turnos (HU26).
 * Solo el admin puede crear reservas para los usuarios.
 * Las franjas son fijas de 2 horas.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ReservationService {

    /**
     * Franjas horarias fijas de 2 horas disponibles en el gimnasio.
     */
    public static final List<String> TIME_SLOTS = Arrays.asList(
            "06:00-08:00",
            "08:00-10:00",
            "10:00-12:00",
            "12:00-14:00",
            "14:00-16:00",
            "16:00-18:00",
            "18:00-20:00",
            "20:00-22:00"
    );

    private final ReservationRepository reservationRepository;
    private final UsersRepository usersRepository;

    /**
     * Devuelve las franjas horarias disponibles.
     */
    public List<String> getAvailableTimeSlots() {
        return TIME_SLOTS;
    }

    /**
     * Crea una reserva en una franja horaria para un usuario.
     * Solo el admin puede invocar este método.
     *
     * @param request DTO con los datos de la reserva
     * @return MessageResponseDTO con el resultado
     */
    @Transactional
    public MessageResponseDTO createReservation(CreateReservationDTO request) {

        // 1. Validar que la franja horaria es válida
        if (!TIME_SLOTS.contains(request.getTimeSlot())) {
            throw new RuntimeException("Franja horaria inválida. Las franjas disponibles son: " + TIME_SLOTS);
        }

        // 2. Validar que el usuario existe
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Validar que el usuario no tiene ya una reserva en esa franja y fecha
        boolean alreadyReserved = reservationRepository.existsActiveByUserAndDateAndTimeSlot(
                request.getUserId(), request.getReservationDate(), request.getTimeSlot());
        if (alreadyReserved) {
            throw new RuntimeException("El usuario ya tiene una reserva en esta franja horaria");
        }

        // 4. Calcular hora inicio y fin a partir de la franja
        LocalTime[] times = parseTimeSlot(request.getTimeSlot());

        // 5. Crear y guardar la reserva
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setReservationDate(request.getReservationDate());
        reservation.setStartTime(times[0]);
        reservation.setEndTime(times[1]);
        reservation.setTimeSlot(request.getTimeSlot());
        reservation.setState(true);
        reservationRepository.save(reservation);

        log.info("Reserva creada para usuario ID: {} en franja: {} del {}",
                user.getIdUser(), request.getTimeSlot(), request.getReservationDate());

        return new MessageResponseDTO("Reserva registrada exitosamente");
    }

    /**
     * Cancela (desactiva) una reserva existente.
     *
     * @param reservationId ID de la reserva a cancelar
     * @return MessageResponseDTO con el resultado
     */
    @Transactional
    public MessageResponseDTO cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reservation.setState(false);
        reservationRepository.save(reservation);

        log.info("Reserva ID: {} cancelada", reservationId);
        return new MessageResponseDTO("Reserva cancelada exitosamente");
    }

    /**
     * Obtiene todas las reservas activas de un usuario.
     *
     * @param userId ID del usuario
     * @return lista de reservas
     */
    public List<Reservation> getReservationsByUser(Long userId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return reservationRepository.findByUserIdUserAndStateTrue(userId);
    }

    /**
     * Parsea una franja horaria en formato "HH:mm-HH:mm" y retorna [inicio, fin].
     */
    private LocalTime[] parseTimeSlot(String timeSlot) {
        String[] parts = timeSlot.split("-");
        return new LocalTime[]{
                LocalTime.parse(parts[0]),
                LocalTime.parse(parts[1])
        };
    }
}