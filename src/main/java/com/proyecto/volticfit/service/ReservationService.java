package com.proyecto.volticfit.service;

import java.time.LocalDate;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.ReservationResponseDTO;
import com.proyecto.volticfit.entity.Reservation;
import com.proyecto.volticfit.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de la gestión de horarios y reservas.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final JwtService jwtService;

    /**
     * Obtiene horarios disponibles según el rol del usuario.
     *
     * @param token JWT del usuario autenticado
     * @param page página actual
     * @param size tamaño de página
     * @return horarios disponibles
     */
    public Page<ReservationResponseDTO> getSchedules(
            String token,
            int page,
            int size
    ) {

       
        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Token inválido");
        }

        String role = jwtService.extractRole(token);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("reservationDate").ascending()
        );

        Page<Reservation> reservations;

        
        if (role.equalsIgnoreCase("admin")) {

            reservations = reservationRepository.findAll(pageable);

        } else {

            reservations =
                    reservationRepository
                            .findByStateTrueAndReservationDateGreaterThanEqual(
                                    LocalDate.now(),
                                    pageable
                            );
        }

        return reservations.map(this::mapToDTO);
    }

    /**
     * Convierte entidad a DTO.
     */
    private ReservationResponseDTO mapToDTO(Reservation reservation) {

        return new ReservationResponseDTO(
                reservation.getIdReservation(),
                reservation.getReservationDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getState(),
                reservation.getUser().getNames()
        );
    }
}