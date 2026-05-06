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

@Service
@Log4j2
@RequiredArgsConstructor
public class SanctionService {
    
    /**
     *  Repositorio de sanciones para acceder a los datos de las sanciones en la base de datos
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

    /**
     * Obtiene todas las sanciones
     * @return Lista de sanciones
     */
    public List<Sanction> getAll() {
        return sanctionRepository.findAll();
    }

    /**
     * Obtiene una sanción por su ID
     * @param id ID de la sanción a obtener
     * @return la saación si se encuenta, de lo contrario lanza una excepción
     */
    public Sanction getById(Long id) {
        return sanctionRepository.findById(id).orElseThrow(() -> new RuntimeException("Sanción no encontrada"));
    }

    /**
     * Obtiene las sanciones de un usuario por su ID
     * @param userId ID del usuario
     * @return lista de sanciones del usuario
     */
    public List<Sanction> getByUser(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User ID must not be null");
        }
        List<Sanction> sanctions = userSanctionRepository.findByUserIdUser(userId)
                .stream()
                .map(us -> us.getSanction())
                .toList();
 
        if (sanctions.isEmpty()) {
            return List.of();
        }
        return sanctions;
    }

    @Transactional
    public MessageResponseDTO create(CreateSanctionDTO request) {
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sanction sanction = new Sanction();
        sanction.setDescription(request.getDescription());
        sanction.setType(request.getType());
        sanction.setStartDate(request.getStartDate());
        sanction.setEndDate(request.getEndDate());
        sanction.setState(true);  
        sanctionRepository.save(sanction);

        UserSanctionId userSanctionId = new UserSanctionId();
        userSanctionId.setUserId(user.getIdUser());
        userSanctionId.setSanctionId(sanction.getIdSanction());

        UserSanction userSanction = new UserSanction();
        userSanction.setId(userSanctionId);
        userSanction.setUser(user);
        userSanction.setSanction(sanction);
        userSanctionRepository.save(userSanction);

        log.info("Sanction created and assigned to user: {}", user.getIdUser());

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Sanction created successfully");
        return response;
    }

    /**
     * Updates an existing sanction.
     *
     * @param id      the sanction ID
     * @param request the data to update
     * @return success message
     */
    @Transactional
    public MessageResponseDTO update(Long id, UpdateSanctionDTO request) {
        Sanction sanction = sanctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sanction not found"));

        if (request.getDescription() != null) sanction.setDescription(request.getDescription());
        if (request.getType() != null) sanction.setType(request.getType());
        if (request.getStartDate() != null) sanction.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) sanction.setEndDate(request.getEndDate());

        sanctionRepository.save(sanction);

        log.info("Sanction updated: {}", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Sanction updated successfully");
        return response;
    }

    /**
     * Deactivates a sanction (logical delete).
     *
     * @param id the sanction ID
     * @return success message
     */
    @Transactional
    public MessageResponseDTO delete(Long id) {
        Sanction sanction = sanctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sanction not found"));

        sanction.setState(false);
        sanctionRepository.save(sanction);

        log.info("Sanction deactivated: {}", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Sanction deactivated successfully");
        return response;
    }
}
