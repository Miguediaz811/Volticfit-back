package com.proyecto.volticfit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.Attendance.AttendanceResponseDTO;
import com.proyecto.volticfit.dto.Attendance.ManualAttendanceRequestDTO;
import com.proyecto.volticfit.dto.QrCode.QrResponseDTO;
import com.proyecto.volticfit.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /*
        Generate QR for user
     */
    @PostMapping("/generate-qr/{userId}")
    public QrResponseDTO generateQR(
            @PathVariable Long userId
    ) {

        return attendanceService.generateQR(userId);
    }

    /*
        Process QR scan
     */
    @PostMapping("/scan")
    public AttendanceResponseDTO processQR(
            @RequestParam String token
    ) {

        return attendanceService.processQRScan(token);
    }

    /*
        Manual attendance
     */
    @PostMapping("/manual")
    public AttendanceResponseDTO processManualAttendance(
            @RequestBody ManualAttendanceRequestDTO request
    ) {

        return attendanceService.processManualAttendance(
                request
        );
    }

    /*
        Find user by document
     */
    @GetMapping("/user")
    public AttendanceResponseDTO findUser(
            @RequestParam String docNumber
    ) {

        return attendanceService.findUserByDoc(
                docNumber
        );
    }
}