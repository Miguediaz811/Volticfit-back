package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByEstado(String estado);
}