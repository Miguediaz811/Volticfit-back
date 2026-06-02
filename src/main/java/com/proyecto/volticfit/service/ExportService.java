package com.proyecto.volticfit.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.entity.Users; 

import com.proyecto.volticfit.repository.MachineRepository;
import com.proyecto.volticfit.repository.SanctionRepository;
import com.proyecto.volticfit.repository.UsersRepository; 

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExportService {

    private final MachineRepository machineRepository;
    private final SanctionRepository sanctionRepository;
    private final UsersRepository usersRepository; 
    
    // Inyecta aquí tus repositorios adicionales en cuanto los uses:
    // private final MaintenanceRepository maintenanceRepository;
    // private final AttendanceRepository attendanceRepository;

    /**
     * HU49: Implementar generación de PDF
     */
    public byte[] generatePdfReport(String targetData) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        
        document.open();
        document.add(new Paragraph("VOLTICFIT MANAGEMENT SYSTEM - REPORT"));
        document.add(new Paragraph("Target scope: " + targetData.toUpperCase()));
        document.add(new Paragraph(" "));

        boolean isGeneral = "general".equalsIgnoreCase(targetData);

        if (isGeneral || "users".equalsIgnoreCase(targetData)) {
            document.add(new Paragraph("--- USER ACCOUNTS ---"));
            document.add(new Paragraph(" "));
            
            PdfPTable table = new PdfPTable(4);
            table.addCell("ID User"); table.addCell("Names"); table.addCell("Email"); table.addCell("Status");
            
            List<Users> users = usersRepository.findAll();
            for (Users u : users) {
                table.addCell(String.valueOf(u.getIdUser()));
                table.addCell(u.getNames() != null ? u.getNames() : "N/A");
                table.addCell(u.getEmail() != null ? u.getEmail() : "N/A");
                
                // Corrección exitosa: Usamos el atributo state real de tu Users.java
                boolean isUserActive = u.getState() != null ? u.getState() : true;
                table.addCell(isUserActive ? "Active" : "Inactive");
            }
            document.add(table);
            document.add(new Paragraph(" "));
        }

        if (isGeneral || "machines".equalsIgnoreCase(targetData)) {
            document.add(new Paragraph("--- GYM MACHINES INVENTORY ---"));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(4);
            table.addCell("ID Machine"); table.addCell("Name"); table.addCell("Type"); table.addCell("Status");
            
            List<Machine> machines = machineRepository.findByStateTrue();
            for (Machine m : machines) {
                table.addCell(String.valueOf(m.getIdMachine()));
                table.addCell(m.getName());
                table.addCell(m.getType());
                table.addCell(m.getState() != null && m.getState() ? "Active" : "Inactive");
            }
            document.add(table);
            document.add(new Paragraph(" "));
        }

        if (isGeneral || "sanctions".equalsIgnoreCase(targetData)) {
            document.add(new Paragraph("--- SYSTEM SANCTIONS RECORD ---"));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(4);
            table.addCell("ID Sanction"); table.addCell("Description"); table.addCell("Type"); table.addCell("Status");
            
            List<Sanction> sanctions = sanctionRepository.findAll();
            for (Sanction s : sanctions) {
                table.addCell(String.valueOf(s.getIdSanction()));
                table.addCell(s.getDescription());
                table.addCell(s.getType());
                table.addCell(s.getState() != null && s.getState() ? "Active" : "Inactive");
            }
            document.add(table);
            document.add(new Paragraph(" "));
        }

        // --- SECCIÓN ESCALABLE DE MANTENIMIENTO Y ASISTENCIAS ---
        if (isGeneral || "maintenance".equalsIgnoreCase(targetData)) {
            document.add(new Paragraph("--- MACHINE MAINTENANCE entries ---"));
            document.add(new Paragraph(" "));
            // Puedes agregar la tabla de mantenimiento inyectando su repositorio correspondiente...
        }

        if (isGeneral || "attendance".equalsIgnoreCase(targetData)) {
            document.add(new Paragraph("--- GYM ATTENDANCE LOGS ---"));
            document.add(new Paragraph(" "));
            // Puedes agregar la tabla de asistencias inyectando su repositorio correspondiente...
        }

        document.close();
        return outputStream.toByteArray();
    }

    /**
     * HU49: Implementar generación de Excel/CSV (Libro de pestañas múltiples)
     */
    public byte[] generateExcelReport(String targetData) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        boolean isGeneral = "general".equalsIgnoreCase(targetData);

        if (isGeneral || "users".equalsIgnoreCase(targetData)) {
            Sheet sheet = workbook.createSheet("Users");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID User");
            header.createCell(1).setCellValue("Names");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Status");
            
            List<Users> list = usersRepository.findAll();
            int idx = 1;
            for (Users u : list) {
                Row row = sheet.createRow(idx++);
                row.createCell(0).setCellValue(u.getIdUser());
                row.createCell(1).setCellValue(u.getNames() != null ? u.getNames() : "N/A");
                row.createCell(2).setCellValue(u.getEmail() != null ? u.getEmail() : "N/A");
                boolean isUserActive = u.getState() != null ? u.getState() : true;
                row.createCell(3).setCellValue(isUserActive ? "Active" : "Inactive");
            }
        }

        if (isGeneral || "machines".equalsIgnoreCase(targetData)) {
            Sheet sheet = workbook.createSheet("Machines");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID Machine");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Status");
            
            List<Machine> list = machineRepository.findByStateTrue  ();
            int idx = 1;
            for (Machine m : list) {
                Row row = sheet.createRow(idx++);
                row.createCell(0).setCellValue(m.getIdMachine());
                row.createCell(1).setCellValue(m.getName());
                row.createCell(2).setCellValue(m.getType());
                row.createCell(3).setCellValue(m.getState() != null && m.getState() ? "Active" : "Inactive");
            }
        }

        if (isGeneral || "sanctions".equalsIgnoreCase(targetData)) {
            Sheet sheet = workbook.createSheet("Sanctions");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID Sanction");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Status");
            
            List<Sanction> list = sanctionRepository.findAll();
            int idx = 1;
            for (Sanction s : list) {
                Row row = sheet.createRow(idx++);
                row.createCell(0).setCellValue(s.getIdSanction());
                row.createCell(1).setCellValue(s.getDescription());
                row.createCell(2).setCellValue(s.getType());
                row.createCell(3).setCellValue(s.getState() != null && s.getState() ? "Active" : "Inactive");
            }
        }

        // Pestañas de mantenimiento o asistencia dinámicas adicionales...
        if (isGeneral || "maintenance".equalsIgnoreCase(targetData)) {
            // Sheet sheet = workbook.createSheet("Maintenance");
        }
        if (isGeneral || "attendance".equalsIgnoreCase(targetData)) {
            // Sheet sheet = workbook.createSheet("Attendance");
        }

        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * HU49: Implementar generación de Excel/CSV (CSV Plano)
     */
    public byte[] generateCsvReport(String targetData) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);
        
        boolean isGeneral = "general".equalsIgnoreCase(targetData);

        if (isGeneral || "users".equalsIgnoreCase(targetData)) {
            writer.println("--- USERS ---");
            writer.println("ID User,Names,Email,Status");
            List<Users> list = usersRepository.findAll();
            for (Users u : list) {
                boolean isUserActive = u.getState() != null ? u.getState() : true;
                writer.println(u.getIdUser() + "," + u.getNames() + "," + u.getEmail() + "," + (isUserActive ? "Active" : "Inactive"));
            }
            writer.println();
        }

        if (isGeneral || "machines".equalsIgnoreCase(targetData)) {
            writer.println("--- MACHINES ---");
            writer.println("ID Machine,Name,Type,Status");
            List<Machine> list = machineRepository.findByStateTrue();
            for (Machine m : list) {
                writer.println(m.getIdMachine() + "," + m.getName() + "," + m.getType() + "," + (m.getState() != null && m.getState() ? "Active" : "Inactive"));
            }
            writer.println();
        }

        if (isGeneral || "sanctions".equalsIgnoreCase(targetData)) {
            writer.println("--- SANCTIONS ---");
            writer.println("ID Sanction,Description,Type,Status");
            List<Sanction> list = sanctionRepository.findAll();
            for (Sanction s : list) {
                writer.println(s.getIdSanction() + "," + s.getDescription() + "," + s.getType() + "," + (s.getState() != null && s.getState() ? "Active" : "Inactive"));
            }
            writer.println();
        }

        writer.flush();
        writer.close();
        return outputStream.toByteArray();
    }
}