package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Reservation.AforoResponseDTO;
import com.proyecto.volticfit.dto.Reservation.CreateReservationDTO;
import com.proyecto.volticfit.dto.Reservation.TimeSlotAforoDTO;
import com.proyecto.volticfit.entity.Reservation;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.ReservationRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio para gestionar el control de aforo por franja horaria (HU27).
 * Las franjas son fijas de 2 horas. El aforo máximo es 20 personas.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ReservationService {

    /** Aforo máximo del gimnasio */
    private static final int MAX_CAPACITY = 20;

    /**
     * Franjas horarias fijas de 2 horas disponibles en el gimnasio.
     */
    private static final List<String> TIME_SLOTS = Arrays.asList(
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
     * Consulta el aforo actual por franja horaria para una fecha específica.
     * Criterio 1 de HU27: siempre muestra aforo por franja.
     * Criterio 3: marca "Aforo completo" si se alcanza el límite.
     *
     * @param date fecha a consultar
     * @return AforoResponseDTO con el detalle de cada franja
     */
    public AforoResponseDTO getAforoByDate(LocalDate date) {
        List<TimeSlotAforoDTO> slots = TIME_SLOTS.stream()
                .map(slot -> buildTimeSlotAforo(slot, date))
                .toList();

        return new AforoResponseDTO(date, slots);
    }

    /**
     * Crea una reserva en una franja horaria validando el aforo disponible.
     * Criterio 2 de HU27: muestra usuarios registrados y ocupación.
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

        // 4. Validar aforo disponible
        long currentOccupancy = reservationRepository.countActiveByDateAndTimeSlot(
                request.getReservationDate(), request.getTimeSlot());

        if (currentOccupancy >= MAX_CAPACITY) {
            throw new RuntimeException("Aforo completo para la franja " + request.getTimeSlot() +
                    ". No hay cupos disponibles.");
        }

        // 5. Calcular hora inicio y fin a partir de la franja
        LocalTime[] times = parseTimeSlot(request.getTimeSlot());

        // 6. Crear y guardar la reserva
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

        long remaining = MAX_CAPACITY - currentOccupancy - 1;
        return new MessageResponseDTO("Reserva registrada exitosamente. Cupos restantes en la franja: " + remaining);
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
     * Construye el DTO de aforo para una franja y fecha específica.
     */
    private TimeSlotAforoDTO buildTimeSlotAforo(String slot, LocalDate date) {
        long occupancy = reservationRepository.countActiveByDateAndTimeSlot(date, slot);
        long available = MAX_CAPACITY - occupancy;
        boolean isFull = occupancy >= MAX_CAPACITY;

        List<String> users = reservationRepository.findActiveByDateAndTimeSlot(date, slot)
                .stream()
                .map(r -> r.getUser().getNames() + " " + r.getUser().getSurnames())
                .toList();

        return new TimeSlotAforoDTO(slot, occupancy, MAX_CAPACITY, available, isFull, users);
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