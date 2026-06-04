package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.SupportTicket.SupportTicketDTO;
import com.proyecto.volticfit.entity.SupportTicket;
import com.proyecto.volticfit.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository repository;

    public SupportTicket startConsulta(Long userId, SupportTicketDTO dto) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUsuarioId(userId);
        ticket.setConsultaTexto(dto.getConsultaTexto());
        
        // Simulación: escalado por palabra clave
        if (dto.getConsultaTexto().toLowerCase().contains("ayuda") || dto.getConsultaTexto().toLowerCase().contains("asesor")) {
            ticket.setEstado("ESCALADA");
        }
        return repository.save(ticket);
    }

    public void escalateToInstructor(Long ticketId) {
        repository.findById(ticketId).ifPresent(ticket -> {
            ticket.setEstado("ESCALADA");
            repository.save(ticket);
        });
    }
}