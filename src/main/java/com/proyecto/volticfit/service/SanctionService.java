package com.proyecto.volticfit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Sanctions.CreateSanctionDTO;
import com.proyecto.volticfit.dto.Sanctions.UpdateSanctionDTO;
import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.entity.UserSanction;
import com.proyecto.volticfit.entity.UserSanctionId;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.SanctionRepository;
import com.proyecto.volticfit.repository.UserSanctionRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service for managing sanctions.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class SanctionService {
    
    /**
     * Repositorio de sanciones para acceder a los datos de las sanciones en la base de datos
     */
    private final SanctionRepository sanctionRepository;

    /**
     * Repositorio de sanciones de usuario para acceder a los datos de las sanciones de los usuarios en la base de datos
     */
    private final UserSanctionRepository userSanctionRepository;

    /**
     * Repositorio de usuarios para acceder a los datos de los usuarios en la base de datos
     */
    private final UsersRepository usersRepository;

@Transactional
    public Sanction create(CreateSanctionDTO request) {
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));

        Sanction sanction = new Sanction();
        sanction.setDescription(request.getDescription());
        sanction.setType(request.getType());
        sanction.setStartDate(request.getStartDate());
        sanction.setEndDate(request.getEndDate());
        sanction.setState(true);

        Sanction savedSanction = sanctionRepository.save(sanction);

        // CORRECCIÓN: Instanciar y asignar propiedades mediante setters para evitar el error de constructor indefinido
        UserSanctionId userSanctionId = new UserSanctionId();
        userSanctionId.setUserId(user.getIdUser());
        userSanctionId.setSanctionId(savedSanction.getIdSanction());

        UserSanction userSanction = new UserSanction();
        userSanction.setId(userSanctionId);
        userSanction.setUser(user);
        userSanction.setSanction(savedSanction);

        userSanctionRepository.save(userSanction);

        log.info("Sanction created and mapped to user: {}", user.getIdUser());

        return savedSanction;
    }

    public List<Sanction> getAll() {
        return sanctionRepository.findAll();
    }

    /**
     * HU42: Implementar actualizarSancion() y Manejar errores de conexión en edición
     */
    @Transactional
    public MessageResponseDTO update(Long id, UpdateSanctionDTO request) {
        Sanction sanction = sanctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la sancion"));

        if (request.getDescription() != null) sanction.setDescription(request.getDescription());
        if (request.getType() != null) sanction.setType(request.getType());
        if (request.getStartDate() != null) sanction.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) sanction.setEndDate(request.getEndDate());

        try {
            sanctionRepository.save(sanction);
        } catch (Exception e) {
            log.error("Falla de red o caída de base de datos durante la edición de la sanción {}: {}", id, e.getMessage());
            throw new IllegalStateException("Error de comunicación con el motor de base de datos al guardar los cambios.", e);
        }

        log.info("Sanction updated: {}", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Sancion actualizada correctamente");
        return response;
    }

    /**
     * HU43: Implementar eliminarSancion() e Implementar manejo de errores de eliminación
     */
    @Transactional
    public MessageResponseDTO delete(Long id) {
        Sanction sanction = sanctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la sancion"));

        try {
            sanctionRepository.delete(sanction);
            log.info("Sanction physically deleted from the database: {}", id);
        } catch (Exception e) {
            log.error("Error crítico de eliminación física para sanción con ID {}: {}", id, e.getMessage());
            throw new IllegalStateException("No se pudo eliminar el registro debido a restricciones de integridad relacional en el sistema.", e);
        }

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Sancion eliminada correctamente");
        return response;
    }
}