package com.proyecto.volticfit.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.proyecto.volticfit.entity.Attendance;
import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.entity.MachineMaintenance;
import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.entity.UserSanction;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.AttendanceRepository;
import com.proyecto.volticfit.repository.MachineMaintenanceRepository;
import com.proyecto.volticfit.repository.MachineRepository;
import com.proyecto.volticfit.repository.SanctionRepository;
import com.proyecto.volticfit.repository.UserSanctionRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service for exporting system data to PDF, Excel and CSV.
 * Only accessible by ADMIN.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ExportService {

    private final UsersRepository usersRepository;
    private final AttendanceRepository attendanceRepository;
    private final MachineRepository machineRepository;
    private final SanctionRepository sanctionRepository;
    private final MachineMaintenanceRepository maintenanceRepository;
    private final UserSanctionRepository userSanctionRepository;

    // =====================
    // PDF EXPORTS
    // =====================

    /**
     * Exports users report as PDF.
     */
    public byte[] exportUsersPdf() {
        List<Users> users = usersRepository.findAll();
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Volticfit - Reporte de Usuarios",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generado: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addPdfHeader(table, "ID", "Nombres", "Apellidos", "Correo", "Estado");

            for (Users u : users) {
                table.addCell(String.valueOf(u.getIdUser()));
                table.addCell(u.getNames());
                table.addCell(u.getSurnames());
                table.addCell(u.getEmail());
                table.addCell(Boolean.TRUE.equals(u.getState()) ? "Activo" : "Inactivo");
            }

            document.add(table);
            document.close();
            log.info("Reporte PDF de usuarios generado");
            return out.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generando PDF de usuarios: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte PDF");
        }
    }

    /**
     * Exports attendance report as PDF.
     */
    public byte[] exportAttendancePdf() {
        return exportAttendancePdf(null, null, null);
    }

    public byte[] exportAttendancePdf(Long userId) {
        return exportAttendancePdf(userId, null, null);
    }

    public byte[] exportAttendancePdf(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = filterAttendance(userId, startDate, endDate);
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Volticfit - Reporte de Asistencia",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generado: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            addPdfHeader(table, "Usuario", "Hora de Entrada", "Hora de Salida", "Tipo");

            for (Attendance a : attendances) {
                table.addCell(a.getUser().getNames() + " " + a.getUser().getSurnames());
                table.addCell(a.getEntryTime() != null ? a.getEntryTime().toString() : "-");
                table.addCell(a.getExitTime() != null ? a.getExitTime().toString() : "-");
                table.addCell(a.getRegistrationType());
            }

            document.add(table);
            document.close();
            log.info("Reporte PDF de asistencia generado");
            return out.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generando PDF de asistencia: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte PDF");
        }
    }

    /**
     * Exports machines report as PDF.
     */
    public byte[] exportMachinesPdf() {
        return exportMachinesPdf(null);
    }

    public byte[] exportMachinesPdf(String status) {
        List<Machine> machines = filterMachines(status);
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Volticfit - Reporte de Máquinas",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generado: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            addPdfHeader(table, "ID", "Nombre", "Tipo", "Estado");

            for (Machine m : machines) {
                table.addCell(String.valueOf(m.getIdMachine()));
                table.addCell(m.getName());
                table.addCell(m.getType());
                table.addCell(Boolean.TRUE.equals(m.getState()) ? "Activo" : "Inactivo");
            }

            document.add(table);
            document.close();
            log.info("Reporte PDF de máquinas generado");
            return out.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generando PDF de máquinas: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte PDF");
        }
    }

    /**
     * Exports sanctions report as PDF.
     */
    public byte[] exportSanctionsPdf() {
        return exportSanctionsPdf(null, null, null, null);
    }

    public byte[] exportSanctionsPdf(Long userId) {
        return exportSanctionsPdf(userId, null, null, null);
    }

    public byte[] exportSanctionsPdf(Long userId, LocalDate startDate, LocalDate endDate, String status) {
        List<UserSanction> sanctions = filterUserSanctions(userId, startDate, endDate, status);
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Volticfit - Reporte de Sanciones",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generado: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            addPdfHeader(table, "Usuario", "Documento", "Tipo", "Descripción", "Fecha Inicio", "Fecha Fin");

            for (UserSanction us : sanctions) {
                Sanction s = us.getSanction();
                Users u = us.getUser();
                table.addCell(u != null ? fullName(u) : "-");
                table.addCell(u != null && u.getDocNum() != null ? u.getDocNum() : "-");
                table.addCell(s.getType());
                table.addCell(s.getDescription() != null ? s.getDescription() : "-");
                table.addCell(s.getStartDate() != null ? s.getStartDate().toString() : "-");
                table.addCell(s.getEndDate() != null ? s.getEndDate().toString() : "-");
            }

            document.add(table);
            document.close();
            log.info("Reporte PDF de sanciones generado");
            return out.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generando PDF de sanciones: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte PDF");
        }
    }

    // =====================
    // EXCEL EXPORTS
    // =====================

    /**
     * Exports users report as Excel.
     */
    public byte[] exportUsersExcel() {
        List<Users> users = usersRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Usuarios");
            createExcelHeader(sheet, "ID", "Nombres", "Apellidos", "Correo", "Estado");

            int rowNum = 1;
            for (Users u : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(u.getIdUser());
                row.createCell(1).setCellValue(u.getNames());
                row.createCell(2).setCellValue(u.getSurnames());
                row.createCell(3).setCellValue(u.getEmail());
                row.createCell(4).setCellValue(Boolean.TRUE.equals(u.getState()) ? "Activo" : "Inactivo");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Reporte Excel de usuarios generado");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error generando Excel de usuarios: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte Excel");
        }
    }

    /**
     * Exports attendance report as Excel.
     */
    public byte[] exportAttendanceExcel() {
        return exportAttendanceExcel(null, null, null);
    }

    public byte[] exportAttendanceExcel(Long userId) {
        return exportAttendanceExcel(userId, null, null);
    }

    public byte[] exportAttendanceExcel(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = filterAttendance(userId, startDate, endDate);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Asistencia");
            createExcelHeader(sheet, "Usuario", "Hora de Entrada", "Hora de Salida", "Tipo");

            int rowNum = 1;
            for (Attendance a : attendances) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(a.getUser().getNames() + " " + a.getUser().getSurnames());
                row.createCell(1).setCellValue(a.getEntryTime() != null ? a.getEntryTime().toString() : "-");
                row.createCell(2).setCellValue(a.getExitTime() != null ? a.getExitTime().toString() : "-");
                row.createCell(3).setCellValue(a.getRegistrationType());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Reporte Excel de asistencia generado");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error generando Excel de asistencia: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte Excel");
        }
    }

    /**
     * Exports machines report as Excel.
     */
    public byte[] exportMachinesExcel() {
        return exportMachinesExcel(null);
    }

    public byte[] exportMachinesExcel(String status) {
        List<Machine> machines = filterMachines(status);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Máquinas");
            createExcelHeader(sheet, "ID", "Nombre", "Tipo", "Estado");

            int rowNum = 1;
            for (Machine m : machines) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getIdMachine());
                row.createCell(1).setCellValue(m.getName());
                row.createCell(2).setCellValue(m.getType());
                row.createCell(3).setCellValue(Boolean.TRUE.equals(m.getState()) ? "Activo" : "Inactivo");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Reporte Excel de máquinas generado");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error generando Excel de máquinas: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte Excel");
        }
    }

    /**
     * Exports sanctions report as Excel.
     */
    public byte[] exportSanctionsExcel() {
        return exportSanctionsExcel(null, null, null, null);
    }

    public byte[] exportSanctionsExcel(Long userId) {
        return exportSanctionsExcel(userId, null, null, null);
    }

    public byte[] exportSanctionsExcel(Long userId, LocalDate startDate, LocalDate endDate, String status) {
        List<UserSanction> sanctions = filterUserSanctions(userId, startDate, endDate, status);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sanciones");
            createExcelHeader(sheet, "Usuario", "Documento", "Tipo", "Descripción", "Fecha Inicio", "Fecha Fin");

            int rowNum = 1;
            for (UserSanction us : sanctions) {
                Sanction s = us.getSanction();
                Users u = us.getUser();
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(u != null ? fullName(u) : "-");
                row.createCell(1).setCellValue(u != null && u.getDocNum() != null ? u.getDocNum() : "-");
                row.createCell(2).setCellValue(s.getType());
                row.createCell(3).setCellValue(s.getDescription() != null ? s.getDescription() : "-");
                row.createCell(4).setCellValue(s.getStartDate() != null ? s.getStartDate().toString() : "-");
                row.createCell(5).setCellValue(s.getEndDate() != null ? s.getEndDate().toString() : "-");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Reporte Excel de sanciones generado");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error generando Excel de sanciones: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte Excel");
        }
    }

    /**
     * Exports maintenance report as PDF.
     */
    public byte[] exportMaintenancePdf() {
        return exportMaintenancePdf(null, null);
    }

    public byte[] exportMaintenancePdf(LocalDate startDate, LocalDate endDate) {
        List<MachineMaintenance> records = filterMaintenance(startDate, endDate);
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Volticfit - Reporte de Mantenimiento",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generado: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            addPdfHeader(table, "Equipo", "Tipo", "Descripción", "Fecha", "Responsable", "Estado");

            for (MachineMaintenance m : records) {
                table.addCell(m.getMachine() != null ? m.getMachine().getName() : "-");
                table.addCell(m.getType() != null ? m.getType() : "-");
                table.addCell(m.getDescription() != null ? m.getDescription() : "-");
                table.addCell(m.getDate() != null ? m.getDate().toString() : "-");
                table.addCell(m.getResponsible() != null ? m.getResponsible() : "-");
                table.addCell(Boolean.TRUE.equals(m.getState()) ? "Activo" : "Cerrado");
            }

            document.add(table);
            document.close();
            log.info("Reporte PDF de mantenimiento generado");
            return out.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generando PDF de mantenimiento: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte PDF de mantenimiento");
        }
    }

    /**
     * Exports maintenance report as Excel.
     */
    public byte[] exportMaintenanceExcel() {
        return exportMaintenanceExcel(null, null);
    }

    public byte[] exportMaintenanceExcel(LocalDate startDate, LocalDate endDate) {
        List<MachineMaintenance> records = filterMaintenance(startDate, endDate);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Mantenimiento");
            createExcelHeader(sheet, "Equipo", "Tipo", "Descripción", "Fecha", "Responsable", "Estado");

            int rowNum = 1;
            for (MachineMaintenance m : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getMachine() != null ? m.getMachine().getName() : "-");
                row.createCell(1).setCellValue(m.getType() != null ? m.getType() : "-");
                row.createCell(2).setCellValue(m.getDescription() != null ? m.getDescription() : "-");
                row.createCell(3).setCellValue(m.getDate() != null ? m.getDate().toString() : "-");
                row.createCell(4).setCellValue(m.getResponsible() != null ? m.getResponsible() : "-");
                row.createCell(5).setCellValue(Boolean.TRUE.equals(m.getState()) ? "Activo" : "Cerrado");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Reporte Excel de mantenimiento generado");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error generando Excel de mantenimiento: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte Excel de mantenimiento");
        }
    }

    /**
     * Exports maintenance report as CSV.
     */
    public byte[] exportMaintenanceCsv() {
        List<MachineMaintenance> records = maintenanceRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("Equipo,Tipo,Descripción,Fecha,Responsable,Estado");
        for (MachineMaintenance m : records) {
            writer.printf("%s,%s,%s,%s,%s,%s%n",
                    m.getMachine() != null ? m.getMachine().getName() : "-",
                    m.getType() != null ? m.getType() : "-",
                    m.getDescription() != null ? m.getDescription() : "-",
                    m.getDate() != null ? m.getDate().toString() : "-",
                    m.getResponsible() != null ? m.getResponsible() : "-",
                    Boolean.TRUE.equals(m.getState()) ? "Activo" : "Cerrado");
        }
        writer.flush();
        log.info("Reporte CSV de mantenimiento generado");
        return out.toByteArray();
    }

    // =====================
    // CSV EXPORTS
    // =====================

    /**
     * Exports users report as CSV.
     */
    public byte[] exportUsersCsv() {
        List<Users> users = usersRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("ID,Nombres,Apellidos,Correo,Estado");
        for (Users u : users) {
            writer.printf("%d,%s,%s,%s,%s%n",
                    u.getIdUser(), u.getNames(), u.getSurnames(),
                    u.getEmail(), Boolean.TRUE.equals(u.getState()) ? "Activo" : "Inactivo");
        }
        writer.flush();
        log.info("Reporte CSV de usuarios generado");
        return out.toByteArray();
    }

    /**
     * Exports attendance report as CSV.
     */
    public byte[] exportAttendanceCsv() {
        List<Attendance> attendances = attendanceRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("Usuario,Hora de Entrada,Hora de Salida,Tipo");
        for (Attendance a : attendances) {
            writer.printf("%s,%s,%s,%s%n",
                    a.getUser().getNames() + " " + a.getUser().getSurnames(),
                    a.getEntryTime() != null ? a.getEntryTime().toString() : "-",
                    a.getExitTime() != null ? a.getExitTime().toString() : "-",
                    a.getRegistrationType());
        }
        writer.flush();
        log.info("Reporte CSV de asistencia generado");
        return out.toByteArray();
    }

    /**
     * Exports machines report as CSV.
     */
    public byte[] exportMachinesCsv() {
        List<Machine> machines = machineRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("ID,Nombre,Tipo,Estado");
        for (Machine m : machines) {
            writer.printf("%d,%s,%s,%s%n",
                    m.getIdMachine(), m.getName(), m.getType(),
                    Boolean.TRUE.equals(m.getState()) ? "Activo" : "Inactivo");
        }
        writer.flush();
        log.info("Reporte CSV de máquinas generado");
        return out.toByteArray();
    }

    /**
     * Exports sanctions report as CSV.
     */
    public byte[] exportSanctionsCsv() {
        List<Sanction> sanctions = sanctionRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("ID,Tipo,Descripción,Fecha Inicio,Fecha Fin");
        for (Sanction s : sanctions) {
            writer.printf("%d,%s,%s,%s,%s%n",
                    s.getIdSanction(), s.getType(),
                    s.getDescription() != null ? s.getDescription() : "-",
                    s.getStartDate() != null ? s.getStartDate().toString() : "-",
                    s.getEndDate() != null ? s.getEndDate().toString() : "-");
        }
        writer.flush();
        log.info("Reporte CSV de sanciones generado");
        return out.toByteArray();
    }

    // =====================
    // Private helpers
    // =====================

    private void addPdfHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private void createExcelHeader(Sheet sheet, String... headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    private List<Attendance> filterAttendance(Long userId) {
        return filterAttendance(userId, null, null);
    }

    private List<Attendance> filterAttendance(Long userId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findAll().stream()
                .filter(attendance -> userId == null
                        || (attendance.getUser() != null && userId.equals(attendance.getUser().getIdUser())))
                .filter(attendance -> {
                    if (startDate == null && endDate == null) return true;
                    if (attendance.getEntryTime() == null) return false;
                    LocalDate entryDate = attendance.getEntryTime().toLocalDate();
                    if (startDate != null && entryDate.isBefore(startDate)) return false;
                    if (endDate != null && entryDate.isAfter(endDate)) return false;
                    return true;
                })
                .sorted((a, b) -> {
                    if (a.getEntryTime() == null) return 1;
                    if (b.getEntryTime() == null) return -1;
                    return b.getEntryTime().compareTo(a.getEntryTime());
                })
                .toList();
    }

    private List<UserSanction> filterUserSanctions(Long userId) {
        return filterUserSanctions(userId, null, null, null);
    }

    private List<UserSanction> filterUserSanctions(Long userId, LocalDate startDate, LocalDate endDate, String status) {
        return userSanctionRepository.findAll().stream()
                .filter(userSanction -> userId == null
                        || (userSanction.getUser() != null && userId.equals(userSanction.getUser().getIdUser())))
                .filter(userSanction -> {
                    Sanction s = userSanction.getSanction();
                    if (s == null) return false;
                    if (status != null && !status.isEmpty()) {
                        boolean wantActive = "activo".equalsIgnoreCase(status);
                        if (s.getState() != wantActive) return false;
                    }
                    if (startDate == null && endDate == null) return true;
                    if (s.getStartDate() == null) return false;
                    LocalDate sDate = s.getStartDate();
                    if (startDate != null && sDate.isBefore(startDate)) return false;
                    if (endDate != null && sDate.isAfter(endDate)) return false;
                    return true;
                })
                .sorted((a, b) -> {
                    LocalDate da = a.getSanction().getStartDate();
                    LocalDate db = b.getSanction().getStartDate();
                    if (da == null) return 1;
                    if (db == null) return -1;
                    return db.compareTo(da);
                })
                .toList();
    }

    private List<MachineMaintenance> filterMaintenance(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.findAll().stream()
                .filter(m -> {
                    if (m.getMachine() == null) return false;
                    String machineName = m.getMachine().getName();
                    if (machineName == null || machineName.trim().equalsIgnoreCase("sin maquina")) return false;
                    if (startDate == null && endDate == null) return true;
                    if (m.getDate() == null) return false;
                    if (startDate != null && m.getDate().isBefore(startDate)) return false;
                    if (endDate != null && m.getDate().isAfter(endDate)) return false;
                    return true;
                })
                .sorted((a, b) -> {
                    if (a.getDate() == null) return 1;
                    if (b.getDate() == null) return -1;
                    return b.getDate().compareTo(a.getDate());
                })
                .toList();
    }

    private List<Machine> filterMachines(String status) {
        return machineRepository.findAll().stream()
                .filter(m -> {
                    String name = m.getName() != null ? m.getName().toLowerCase() : "";
                    String type = m.getType() != null ? m.getType().toLowerCase() : "";
                    if (name.equals("sin maquina") || type.equals("peso corporal")) return false;
                    if (status != null && !status.isEmpty()) {
                        boolean wantActive = "activo".equalsIgnoreCase(status);
                        return m.getState() == wantActive;
                    }
                    return true;
                })
                .toList();
    }

    private String fullName(Users user) {
        String names = user.getNames() != null ? user.getNames() : "";
        String surnames = user.getSurnames() != null ? user.getSurnames() : "";
        String fullName = (names + " " + surnames).trim();
        return fullName.isBlank() ? "-" : fullName;
    }
}
