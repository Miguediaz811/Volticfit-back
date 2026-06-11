package com.proyecto.volticfit.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.Attendance.AttendanceRequestDTO;
import com.proyecto.volticfit.dto.Attendance.AttendanceResponseDTO;
import com.proyecto.volticfit.dto.Attendance.AttendanceListResponseDTO;
import com.proyecto.volticfit.dto.Attendance.ManualAttendanceRequestDTO;
import com.proyecto.volticfit.dto.QrCode.QrGeneratedResponseDTO;
import com.proyecto.volticfit.service.AttendanceService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

 
    private final AttendanceService attendanceService;
 
    /**
     * Generates a QR code for the given user to register attendance.
     *
     * @param userId the user ID as a query parameter
     * @return QrResponseDTO with base64 image and token
     */
    @GetMapping("/qr")
    public ResponseEntity<QrGeneratedResponseDTO> generateQR(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(attendanceService.generateQR(userId));
    }
 
    /**
     * Processes a QR scan for entry or exit.
     *
     * @param request AttendanceRequestDTO containing the QR token
     * @return AttendanceResponseDTO with the result
     */
    @PostMapping("/qr/scan")
    public ResponseEntity<AttendanceResponseDTO> scanQR(
            @RequestBody AttendanceRequestDTO request
    ) {
        try {
            return ResponseEntity.ok(attendanceService.processQRScan(request.getToken()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AttendanceResponseDTO("ERROR", e.getMessage(), null, null, null, null, null, null, null, null));
        }
    }
 
    /**
     * Registers attendance manually by document number.
     *
     * @param request ManualAttendanceRequestDTO with the document number
     * @return AttendanceResponseDTO with the result
     */
    @PostMapping("/manual")
    public ResponseEntity<AttendanceResponseDTO> manualAttendance(
            @RequestBody ManualAttendanceRequestDTO request
    ) {
        try {
            return ResponseEntity.ok(attendanceService.processManualAttendance(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AttendanceResponseDTO("ERROR", e.getMessage(), null, null, null, null, null, null, null, null));
        }
    }
 
    /**
     * Looks up a user by document number before registering manual attendance.
     *
     * @param docNumber the user's document number
     * @return AttendanceResponseDTO with user info
     */
    @GetMapping("/manual/search")
    public ResponseEntity<AttendanceResponseDTO> findUserByDoc(
            @RequestParam String docNumber
    ) {
        return ResponseEntity.ok(attendanceService.findUserByDoc(docNumber));
    }
 
    /**
     * Returns the paginated attendance history for the authenticated user.
     *
     * @param authHeader Authorization header with Bearer token
     * @param page       page number (default 0)
     * @param size       page size (default 10)
     * @return paginated attendance records
     */
    @GetMapping("/history")
    public ResponseEntity<Page<AttendanceListResponseDTO>> getAttendanceHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(attendanceService.getAttendanceHistory(authHeader, page, size));
    }

    /**
     * Returns all attendance records for all users (admin use).
     * Optionally filtered by startDate and endDate (format: yyyy-MM-dd).
     */
    @GetMapping("/all")
    public ResponseEntity<List<AttendanceListResponseDTO>> getAllAttendance(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long userId,
            HttpServletRequest httpRequest
    ) {
        String role = (String) httpRequest.getAttribute("role");
        if (!"admin".equalsIgnoreCase(role) && !"instructor".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end   = endDate   != null ? LocalDate.parse(endDate)   : null;
        if (userId != null) {
            return ResponseEntity.ok(attendanceService.getAllAttendanceByUser(userId, start, end));
        }
        return ResponseEntity.ok(attendanceService.getAllAttendance(start, end));
    }
}