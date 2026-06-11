package com.proyecto.volticfit.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.proyecto.volticfit.dto.Attendance.AttendanceListResponseDTO;
import com.proyecto.volticfit.dto.Attendance.AttendanceResponseDTO;
import com.proyecto.volticfit.dto.Attendance.ManualAttendanceRequestDTO;
import com.proyecto.volticfit.dto.QrCode.QrGeneratedResponseDTO;
import com.proyecto.volticfit.entity.Attendance;
import com.proyecto.volticfit.entity.QrCode;
import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.entity.UserSanction;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.AttendanceRepository;
import com.proyecto.volticfit.repository.QrCodeRepository;
import com.proyecto.volticfit.repository.UserSanctionRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service for managing gym attendance via QR and manual registration.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AttendanceService {

    // QR code image size
    private static final int QR_SIZE = 300;

    // Repository for users to manage user data and lookups
    private final UsersRepository usersRepository;

    // Repository for QR codes to manage generation and validation
    private final QrCodeRepository qrCodeRepository;

    // Repository for attendance records to manage entry and exit times
    private final AttendanceRepository attendanceRepository;

    // Repository for user sanctions to check active sanctions during attendance registration
    private final UserSanctionRepository userSanctionRepository;

    // JWT service for validating tokens in attendance history retrieval
    private final JwtService jwtService;

    /**
     * Generates a unique QR code for a user's gym visit.
     * If the user already has an active QR, returns it.
     *
     * @param userId the user ID
     * @return QrResponseDTO with base64 image and token
     */
    @Transactional
    public QrGeneratedResponseDTO generateQR(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));

        // Check if user already has an active QR
        Optional<QrCode> existingQR = qrCodeRepository.findByUserIdUserAndUsed(userId, false);
        if (existingQR.isPresent()) {
            return buildQRResponse(existingQR.get().getToken());
        }

        // Generate new unique token
        String token = UUID.randomUUID().toString();

        QrCode qrCode = new QrCode();
        qrCode.setUser(user);
        qrCode.setToken(token);
        qrCode.setUsed(false);
        qrCodeRepository.save(qrCode);

        log.info("QR generated for user: {}", userId);
        return buildQRResponse(token);
    }

    /**
     * Processes a QR scan for entry or exit.
     *
     * @param token the QR token scanned
     * @return AttendanceResponseDTO with result
     */
    @Transactional
    public AttendanceResponseDTO processQRScan(String token) {
        QrCode qrCode = qrCodeRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("El codigo QR no es valido"));

        if (qrCode.getUsed()) {
            throw new RuntimeException("Este codigo QR ya fue utilizado");
        }

        Users user = qrCode.getUser();

        // Check if user has an open attendance (EXIT scan)
        Optional<Attendance> openAttendance = attendanceRepository
                .findByUserIdUserAndExitTimeIsNull(user.getIdUser());
        if (openAttendance.isPresent()) {
            return registerExit(openAttendance.get(), qrCode, user);
        }

        // ENTRY: validate sanction
        Optional<Sanction> activeSanction = getActiveSanction(user.getIdUser());
        if (activeSanction.isPresent()) {
            return buildSanctionResponse(user, activeSanction.get());
        }

        return registerEntry(user, qrCode);
    }

    /**
     * Processes manual attendance registration by document number.
     *
     * @param request the manual attendance request
     * @return AttendanceResponseDTO with result
     */
    @Transactional
    public AttendanceResponseDTO processManualAttendance(ManualAttendanceRequestDTO request) {
        Users user = usersRepository.findByDocNum(request.getDocNumber())
                .orElseThrow(() -> new RuntimeException("No se encontro un usuario con ese documento"));

        // Check if user has open attendance (EXIT)
        Optional<Attendance> openAttendance = attendanceRepository
                .findByUserIdUserAndExitTimeIsNull(user.getIdUser());
        if (openAttendance.isPresent()) {
            Attendance attendance = openAttendance.get();
            attendance.setExitTime(LocalDateTime.now());
            attendanceRepository.save(attendance);
            log.info("Manual exit registered for user: {}", user.getIdUser());
            return buildValidResponse(user, "EXIT_REGISTERED", "Salida registrada correctamente");
        }

        // Validate sanction
        Optional<Sanction> activeSanction = getActiveSanction(user.getIdUser());
        if (activeSanction.isPresent()) {
            return buildSanctionResponse(user, activeSanction.get());
        }

        // Register entry
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setEntryTime(LocalDateTime.now());
        attendance.setRegistrationType("MANUAL");
        attendanceRepository.save(attendance);

        log.info("Manual entry registered for user: {}", user.getIdUser());
        return buildValidResponse(user, "VALID", "Entrada registrada correctamente");
    }

    /**
     * Finds a user by document number for the manual search step.
     *
     * @param docNumber the document number
     * @return the user data response
     */
    public AttendanceResponseDTO findUserByDoc(String docNumber) {
        Users user = usersRepository.findByDocNum(docNumber)
                .orElseThrow(() -> new RuntimeException("No se encontro un usuario con ese documento"));
        return buildValidResponse(user, "FOUND", "Usuario encontrado");
    }

    /**
     * Returns paginated attendance history for the authenticated user.
     *
     * @param authHeader the Authorization header with Bearer token
     * @param page       page number (0-based)
     * @param size       page size
     * @return paginated list of attendance records
     */
    public Page<AttendanceListResponseDTO> getAttendanceHistory(String authHeader, int page, int size) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("La sesion ya no es valida");
        }

        String email = jwtService.extractEmail(token);

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("entryTime").descending());

        return attendanceRepository.findByUserIdUser(user.getIdUser(), pageable)
                .map(att -> new AttendanceListResponseDTO(
                        att.getIdAttendance(),
                        user.getNames() + " " + user.getSurnames(),
                        user.getDocNum(),
                        att.getEntryTime(),
                        att.getExitTime(),
                        att.getRegistrationType()
                ));
    }

    // --- Private helpers ---

    private AttendanceResponseDTO registerEntry(Users user, QrCode qrCode) {
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setQrCode(qrCode);
        attendance.setEntryTime(LocalDateTime.now());
        attendance.setRegistrationType("QR");
        attendanceRepository.save(attendance);
        log.info("QR entry registered for user: {}", user.getIdUser());
        return buildValidResponse(user, "VALID", "Entrada registrada correctamente");
    }

    private AttendanceResponseDTO registerExit(Attendance attendance, QrCode qrCode, Users user) {
        attendance.setExitTime(LocalDateTime.now());
        attendanceRepository.save(attendance);
        qrCode.setUsed(true);
        qrCodeRepository.save(qrCode);
        log.info("QR exit registered for user: {}", user.getIdUser());
        return buildValidResponse(user, "EXIT_REGISTERED", "Salida registrada correctamente");
    }

    private Optional<Sanction> getActiveSanction(Long userId) {
        LocalDate today = LocalDate.now();
        List<UserSanction> userSanctions = userSanctionRepository.findByUserIdUser(userId);
        return userSanctions.stream()
                .map(UserSanction::getSanction)
                .filter(s -> s.getState()
                        && s.getStartDate() != null
                        && s.getEndDate() != null
                        && !today.isBefore(s.getStartDate())
                        && !today.isAfter(s.getEndDate()))
                .findFirst();
    }

    private AttendanceResponseDTO buildValidResponse(Users user, String status, String message) {
        AttendanceResponseDTO response = new AttendanceResponseDTO();
        response.setStatus(status);
        response.setMessage(message);
        response.setNames(user.getNames());
        response.setLastNames(user.getSurnames());
        response.setPhone(user.getPhone() != null ? user.getPhone().toString() : "");
        response.setDocType(user.getDocType());
        response.setDocNumber(user.getDocNum());
        return response;
    }

    private AttendanceResponseDTO buildSanctionResponse(Users user, Sanction sanction) {
        AttendanceResponseDTO response = buildValidResponse(user, "INVALID_SANCTION",
                "Acceso bloqueado por sancion activa");
        response.setSanctionStartDate(sanction.getStartDate().toString());
        response.setSanctionEndDate(sanction.getEndDate().toString());
        response.setSanctionDescription(sanction.getDescription());
        return response;
    }

    /**
     * Returns all attendance records for all users (admin only), optionally filtered by date range.
     *
     * @param startDate optional start date filter (inclusive)
     * @param endDate   optional end date filter (inclusive)
     * @return list of attendance records with user info
     */
    public List<AttendanceListResponseDTO> getAllAttendance(LocalDate startDate, LocalDate endDate) {
        return filterAndMapAttendance(null, startDate, endDate);
    }

    /**
     * Returns all attendance records for a specific user, optionally filtered by date range.
     *
     * @param userId    the user ID to filter by
     * @param startDate optional start date filter (inclusive)
     * @param endDate   optional end date filter (inclusive)
     * @return list of attendance records for the given user
     */
    public List<AttendanceListResponseDTO> getAllAttendanceByUser(Long userId, LocalDate startDate, LocalDate endDate) {
        return filterAndMapAttendance(userId, startDate, endDate);
    }

    private List<AttendanceListResponseDTO> filterAndMapAttendance(Long userId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findAll().stream()
                .filter(att -> {
                    if (userId != null && (att.getUser() == null || !att.getUser().getIdUser().equals(userId)))
                        return false;
                    if (startDate == null && endDate == null) return true;
                    LocalDate entryDate = att.getEntryTime() != null ? att.getEntryTime().toLocalDate() : null;
                    if (entryDate == null) return false;
                    if (startDate != null && entryDate.isBefore(startDate)) return false;
                    if (endDate != null && entryDate.isAfter(endDate)) return false;
                    return true;
                })
                .sorted((a, b) -> b.getEntryTime().compareTo(a.getEntryTime()))
                .map(att -> {
                    Users user = att.getUser();
                    String fullName = user == null ? "-"
                            : ((user.getNames() == null ? "" : user.getNames()) + " "
                               + (user.getSurnames() == null ? "" : user.getSurnames())).trim();
                    return new AttendanceListResponseDTO(
                            att.getIdAttendance(),
                            fullName,
                            user == null ? "-" : user.getDocNum(),
                            att.getEntryTime(),
                            att.getExitTime(),
                            att.getRegistrationType()
                    );
                })
                .toList();
    }

    private QrGeneratedResponseDTO buildQRResponse(String token) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(token, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return new QrGeneratedResponseDTO("data:image/png;base64," + base64, token);
        } catch (Exception e) {
            log.error("Error generating QR image: {}", e.getMessage());
            throw new RuntimeException("No se pudo generar el codigo QR");
        }
    }
}