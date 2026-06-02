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
import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.AttendanceRepository;
import com.proyecto.volticfit.repository.MachineRepository;
import com.proyecto.volticfit.repository.SanctionRepository;
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

            document.add(new Paragraph("Volticfit - Users Report",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generated: " + LocalDate.now(), 
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addPdfHeader(table, "ID", "Names", "Surnames", "Email", "Status");

            for (Users u : users) {
                table.addCell(String.valueOf(u.getIdUser()));
                table.addCell(u.getNames());
                table.addCell(u.getSurnames());
                table.addCell(u.getEmail());
                table.addCell(Boolean.TRUE.equals(u.getState()) ? "Active" : "Inactive");
            }

            document.add(table);
            document.close();
            log.info("Users PDF report generated");
            return out.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generating users PDF: {}", e.getMessage());
            throw new RuntimeException("Error generating PDF report");
        }
    }

    /**
     * Exports attendance report as PDF.
     */
    public byte[] exportAttendancePdf() {
        List<Attendance> attendances = attendanceRepository.findAll();
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Volticfit - Attendance Report",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generated: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            addPdfHeader(table, "User", "Entry Time", "Exit Time", "Type");

            for (Attendance a : attendances) {
                table.addCell(a.getUser().getNames() + " " + a.getUser().getSurnames());
                table.addCell(a.getEntryTime() != null ? a.getEntryTime().toString() : "-");
                table.addCell(a.getExitTime() != null ? a.getExitTime().toString() : "-");
                table.addCell(a.getRegistrationType());
            }

            document.add(table);
            document.close();
            log.info("Attendance PDF report generated");
            return out.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generating attendance PDF: {}", e.getMessage());
            throw new RuntimeException("Error generating PDF report");
        }
    }

    /**
     * Exports machines report as PDF.
     */
    public byte[] exportMachinesPdf() {
        List<Machine> machines = machineRepository.findAll();
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Volticfit - Machines Report",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generated: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            addPdfHeader(table, "ID", "Name", "Type", "Status");

            for (Machine m : machines) {
                table.addCell(String.valueOf(m.getIdMachine()));
                table.addCell(m.getName());
                table.addCell(m.getType());
                table.addCell(Boolean.TRUE.equals(m.getState()) ? "Active" : "Inactive");
            }

            document.add(table);
            document.close();
            log.info("Machines PDF report generated");
            return out.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generating machines PDF: {}", e.getMessage());
            throw new RuntimeException("Error generating PDF report");
        }
    }

    /**
     * Exports sanctions report as PDF.
     */
    public byte[] exportSanctionsPdf() {
        List<Sanction> sanctions = sanctionRepository.findAll();
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Volticfit - Sanctions Report",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generated: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addPdfHeader(table, "ID", "Type", "Description", "Start Date", "End Date");

            for (Sanction s : sanctions) {
                table.addCell(String.valueOf(s.getIdSanction()));
                table.addCell(s.getType());
                table.addCell(s.getDescription() != null ? s.getDescription() : "-");
                table.addCell(s.getStartDate() != null ? s.getStartDate().toString() : "-");
                table.addCell(s.getEndDate() != null ? s.getEndDate().toString() : "-");
            }

            document.add(table);
            document.close();
            log.info("Sanctions PDF report generated");
            return out.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generating sanctions PDF: {}", e.getMessage());
            throw new RuntimeException("Error generating PDF report");
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
            Sheet sheet = workbook.createSheet("Users");
            createExcelHeader(sheet, "ID", "Names", "Surnames", "Email", "Status");

            int rowNum = 1;
            for (Users u : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(u.getIdUser());
                row.createCell(1).setCellValue(u.getNames());
                row.createCell(2).setCellValue(u.getSurnames());
                row.createCell(3).setCellValue(u.getEmail());
                row.createCell(4).setCellValue(Boolean.TRUE.equals(u.getState()) ? "Active" : "Inactive");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Users Excel report generated");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error generating users Excel: {}", e.getMessage());
            throw new RuntimeException("Error generating Excel report");
        }
    }

    /**
     * Exports attendance report as Excel.
     */
    public byte[] exportAttendanceExcel() {
        List<Attendance> attendances = attendanceRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");
            createExcelHeader(sheet, "User", "Entry Time", "Exit Time", "Type");

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
            log.info("Attendance Excel report generated");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error generating attendance Excel: {}", e.getMessage());
            throw new RuntimeException("Error generating Excel report");
        }
    }

    /**
     * Exports machines report as Excel.
     */
    public byte[] exportMachinesExcel() {
        List<Machine> machines = machineRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Machines");
            createExcelHeader(sheet, "ID", "Name", "Type", "Status");

            int rowNum = 1;
            for (Machine m : machines) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getIdMachine());
                row.createCell(1).setCellValue(m.getName());
                row.createCell(2).setCellValue(m.getType());
                row.createCell(3).setCellValue(Boolean.TRUE.equals(m.getState()) ? "Active" : "Inactive");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Machines Excel report generated");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error generating machines Excel: {}", e.getMessage());
            throw new RuntimeException("Error generating Excel report");
        }
    }

    /**
     * Exports sanctions report as Excel.
     */
    public byte[] exportSanctionsExcel() {
        List<Sanction> sanctions = sanctionRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sanctions");
            createExcelHeader(sheet, "ID", "Type", "Description", "Start Date", "End Date");

            int rowNum = 1;
            for (Sanction s : sanctions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getIdSanction());
                row.createCell(1).setCellValue(s.getType());
                row.createCell(2).setCellValue(s.getDescription() != null ? s.getDescription() : "-");
                row.createCell(3).setCellValue(s.getStartDate() != null ? s.getStartDate().toString() : "-");
                row.createCell(4).setCellValue(s.getEndDate() != null ? s.getEndDate().toString() : "-");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Sanctions Excel report generated");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error generating sanctions Excel: {}", e.getMessage());
            throw new RuntimeException("Error generating Excel report");
        }
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
        writer.println("ID,Names,Surnames,Email,Status");
        for (Users u : users) {
            writer.printf("%d,%s,%s,%s,%s%n",
                    u.getIdUser(), u.getNames(), u.getSurnames(),
                    u.getEmail(), Boolean.TRUE.equals(u.getState()) ? "Active" : "Inactive");
        }
        writer.flush();
        log.info("Users CSV report generated");
        return out.toByteArray();
    }

    /**
     * Exports attendance report as CSV.
     */
    public byte[] exportAttendanceCsv() {
        List<Attendance> attendances = attendanceRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("User,Entry Time,Exit Time,Type");
        for (Attendance a : attendances) {
            writer.printf("%s,%s,%s,%s%n",
                    a.getUser().getNames() + " " + a.getUser().getSurnames(),
                    a.getEntryTime() != null ? a.getEntryTime().toString() : "-",
                    a.getExitTime() != null ? a.getExitTime().toString() : "-",
                    a.getRegistrationType());
        }
        writer.flush();
        log.info("Attendance CSV report generated");
        return out.toByteArray();
    }

    /**
     * Exports machines report as CSV.
     */
    public byte[] exportMachinesCsv() {
        List<Machine> machines = machineRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("ID,Name,Type,Status");
        for (Machine m : machines) {
            writer.printf("%d,%s,%s,%s%n",
                    m.getIdMachine(), m.getName(), m.getType(),
                    Boolean.TRUE.equals(m.getState()) ? "Active" : "Inactive");
        }
        writer.flush();
        log.info("Machines CSV report generated");
        return out.toByteArray();
    }

    /**
     * Exports sanctions report as CSV.
     */
    public byte[] exportSanctionsCsv() {
        List<Sanction> sanctions = sanctionRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("ID,Type,Description,Start Date,End Date");
        for (Sanction s : sanctions) {
            writer.printf("%d,%s,%s,%s,%s%n",
                    s.getIdSanction(), s.getType(),
                    s.getDescription() != null ? s.getDescription() : "-",
                    s.getStartDate() != null ? s.getStartDate().toString() : "-",
                    s.getEndDate() != null ? s.getEndDate().toString() : "-");
        }
        writer.flush();
        log.info("Sanctions CSV report generated");
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
}