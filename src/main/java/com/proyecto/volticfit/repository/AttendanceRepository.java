package com.proyecto.volticfit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
 
    /**
     * Find all attendance records for a specific user.
     *
     * @param userId the user ID
     * @return list of attendance records
     */
    List<Attendance> findByUserIdUser(Long userId);
 
    /**
     * Find the open attendance record (no exit time) for a user.
     *
     * @param userId the user ID
     * @return an Optional containing the open attendance if found
     */
    Optional<Attendance> findByUserIdUserAndExitTimeIsNull(Long userId);
}
