package com.proyecto.volticfit.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Support.CreateSupportTicketDTO;
import com.proyecto.volticfit.dto.Support.SupportTicketDTO;
import com.proyecto.volticfit.entity.SupportTicket;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.SupportTicketRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CO"));

    private final UsersRepository usersRepository;
    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketDTO create(CreateSupportTicketDTO request, Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));
        LocalDateTime now = LocalDateTime.now();
        String text = request.getSubject() + "\n\n" + request.getDescription();
        if (request.getAttachment() != null && !request.getAttachment().isBlank()) {
            text += "\n\nAdjunto: " + request.getAttachment();
        }

        SupportTicket ticket = new SupportTicket();
        ticket.setUsuarioId(user.getIdUser());
        ticket.setConsultaTexto(text);
        ticket.setEstado("ESCALADA");
        ticket.setFechaInicio(now);
        return toDto(supportTicketRepository.save(ticket), user);
    }


    public List<SupportTicketDTO> getMyTickets(Long userId) {
        return supportTicketRepository.findByUsuarioIdOrderByFechaInicioDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<SupportTicketDTO> getAll(String role) {
        requireAdmin(role);
        return supportTicketRepository.findAll().stream()
                .map(this::toDto)
                .sorted(Comparator.comparing(SupportTicketDTO::getCreatedAt).reversed())
                .toList();
    }

    public MessageResponseDTO updateStatus(String code, String status, String role) {
        requireAdmin(role);
        SupportTicket ticket = findTicket(code);
        ticket.setEstado(normalizeStatus(status));
        supportTicketRepository.save(ticket);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Estado de consulta actualizado");
        return response;
    }

    public MessageResponseDTO reply(String code, String message, String role, Long userId) {
        SupportTicket ticket = findTicket(code);
        boolean isAdmin = "admin".equalsIgnoreCase(role);
        boolean isOwner = ticket.getUsuarioId() != null && ticket.getUsuarioId().equals(userId);
        if (!isAdmin && !isOwner) {
            throw new RuntimeException("No tienes permiso para responder esta consulta");
        }
        String prefix = isAdmin ? "\n\nRespuesta admin: " : "\n\nRespuesta usuario: ";
        ticket.setConsultaTexto(ticket.getConsultaTexto() + prefix + message);
        if (isAdmin) ticket.setEstado("EN_REVISION");
        supportTicketRepository.save(ticket);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage(isAdmin ? "Respuesta enviada al usuario" : "Respuesta enviada al instructor");
        return response;
    }

    private SupportTicket findTicket(String code) {
        return supportTicketRepository.findById(extractId(code))
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
    }

    private void requireAdmin(String role) {
        if (!"admin".equalsIgnoreCase(role)) {
            throw new RuntimeException("No tienes permiso para gestionar consultas");
        }
    }

    private String normalizeStatus(String status) {
        String value = status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "escalada" -> "ESCALADA";
            case "en revision", "en revisión", "revision", "revisión", "en_revision" -> "EN_REVISION";
            case "resuelta", "resuelto" -> "RESUELTA";
            default -> throw new RuntimeException("Estado de consulta invalido");
        };
    }

    private SupportTicketDTO toDto(SupportTicket ticket) {
        Users user = ticket.getUsuarioId() == null
                ? null
                : usersRepository.findById(ticket.getUsuarioId()).orElse(null);
        return toDto(ticket, user);
    }

    private SupportTicketDTO toDto(SupportTicket ticket, Users user) {
        String text = ticket.getConsultaTexto() == null ? "" : ticket.getConsultaTexto();

        // Separar turns: cada bloque separado por \n\n (inicio) o prefijos conocidos
        // El primer bloque es asunto\n\ndescripcion, luego vienen respuestas
        String instructorResponse = null;
        String bodyText = text;

        // Encontrar la ultima respuesta del admin para el campo legacy
        int lastAdminIdx = text.lastIndexOf("\n\nRespuesta admin: ");
        if (lastAdminIdx >= 0) {
            instructorResponse = text.substring(lastAdminIdx + "\n\nRespuesta admin: ".length()).trim();
            bodyText = text.substring(0, lastAdminIdx);
        }

        // Separar asunto y descripcion del bloque inicial
        // El bloque inicial puede tener mas respuestas de usuario entre medio
        String initialBlock = bodyText;
        int firstUserReply = bodyText.indexOf("\n\nRespuesta usuario: ");
        if (firstUserReply >= 0) {
            initialBlock = bodyText.substring(0, firstUserReply);
        }

        String[] parts = initialBlock.split("\\n\\n", 2);
        String subject = parts.length > 0 && !parts[0].isBlank() ? parts[0] : "Consulta con instructor";
        String message = parts.length > 1 ? parts[1] : initialBlock;

        String userName = user == null ? "-" : ((user.getNames() == null ? "" : user.getNames()) + " "
                + (user.getSurnames() == null ? "" : user.getSurnames())).trim();

        return new SupportTicketDTO(
                "SOP-" + ticket.getId(),
                ticket.getUsuarioId(),
                userName.isBlank() && user != null ? user.getEmail() : userName,
                subject,
                message,
                displayStatus(ticket.getEstado()),
                ticket.getFechaInicio() == null ? "" : DISPLAY_FORMAT.format(ticket.getFechaInicio()),
                ticket.getConsultaTexto(),
                instructorResponse);
    }

    private String displayStatus(String status) {
        String value = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        return switch (value) {
            case "ESCALADA", "PENDIENTE" -> "Escalada";
            case "EN_REVISION" -> "En revision";
            case "RESUELTA" -> "Resuelta";
            default -> "Escalada";
        };
    }

    private Long extractId(String code) {
        try {
            return Long.parseLong(code.replace("SOP-", "").trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Codigo de consulta invalido");
        }
    }
}