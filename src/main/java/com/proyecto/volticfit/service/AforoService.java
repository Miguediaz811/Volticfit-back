package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.Reservation.AforoResponseDTO;
import com.proyecto.volticfit.dto.Reservation.TimeSlotAforoDTO;
import com.proyecto.volticfit.entity.Attendance;
import com.proyecto.volticfit.repository.AttendanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio para gestionar el control de aforo del gimnasio (HU27).
 * Máximo 20 personas dentro del gimnasio al mismo tiempo.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AforoService {

    private static final int MAX_CAPACITY = 20;

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

    private final AttendanceRepository attendanceRepository;

    public AforoResponseDTO getAforoByDate(LocalDate date) {
        List<Attendance> todayAttendances = attendanceRepository.findByDateOrderByTimeAsc(date);

        // Paso 1: Por cada usuario, obtener su último registro del día
        Map<Long, Attendance> lastRecordPerUser = todayAttendances.stream()
                .collect(Collectors.toMap(
                        a -> a.getUser().getIdUser(),
                        a -> a,
                        (a1, a2) -> a1.getTime().isAfter(a2.getTime()) ? a1 : a2
                ));

        // Paso 2: Filtrar solo los que su último registro es ENTRADA (están dentro)
        List<Attendance> usersInside = lastRecordPerUser.values().stream()
                .filter(a -> "ENTRADA".equals(a.getType()))
                .toList();

        log.info("Usuarios actualmente dentro del gimnasio: {}", usersInside.size());

        // Paso 3: Construir aforo por franja según la hora de entrada
        List<TimeSlotAforoDTO> slots = TIME_SLOTS.stream()
                .map(slot -> buildTimeSlotAforo(slot, usersInside))
                .toList();

        return new AforoResponseDTO(date, slots);
    }

    private TimeSlotAforoDTO buildTimeSlotAforo(String slot, List<Attendance> usersInside) {
        LocalTime[] times = parseTimeSlot(slot);
        LocalTime slotStart = times[0];
        LocalTime slotEnd = times[1];

        // Usuarios que entraron en esta franja y siguen adentro
        List<String> usersInSlot = usersInside.stream()
                .filter(a -> {
                    LocalTime entryTime = a.getTime();
                    return !entryTime.isBefore(slotStart) && entryTime.isBefore(slotEnd);
                })
                .map(a -> a.getUser().getNames() + " " + a.getUser().getSurnames())
                .toList();

        long occupancy = usersInSlot.size();
        long available = MAX_CAPACITY - occupancy;
        boolean isFull = occupancy >= MAX_CAPACITY;

        return new TimeSlotAforoDTO(slot, occupancy, MAX_CAPACITY, available, isFull, usersInSlot);
    }

    private LocalTime[] parseTimeSlot(String timeSlot) {
        String[] parts = timeSlot.split("-");
        return new LocalTime[]{
                LocalTime.parse(parts[0]),
                LocalTime.parse(parts[1])
        };
    }
}