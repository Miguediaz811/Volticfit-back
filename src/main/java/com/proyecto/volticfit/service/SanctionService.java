package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Sanctions.CreateSanctionDTO;
import com.proyecto.volticfit.dto.Sanctions.SanctionWithUserDTO;
import com.proyecto.volticfit.dto.Sanctions.UpdateSanctionDTO;
import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.entity.UserSanction;
import com.proyecto.volticfit.entity.UserSanctionId;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.enums.RoleEnum;
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
     * Devuelve todas las sanciones con los datos del usuario vinculado.
     */
    public List<SanctionWithUserDTO> getAllWithUser() {
        return userSanctionRepository.findAll().stream()
                .map(us -> toDTO(us.getSanction(), us.getUser()))
                .collect(Collectors.toList());
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
            throw new RuntimeException("No se pudo identificar el usuario");
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
        validateDates(request.getStartDate(), request.getEndDate());

        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));

        if (isAdmin(user)) {
            throw new RuntimeException("No se puede sancionar a un administrador");
        }

        Sanction sanction = new Sanction();
        sanction.setDescription(request.getDescription());
        sanction.setType(request.getType());
        sanction.setClasificacion(request.getClasificacion());
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
        response.setMessage("Sancion registrada correctamente");
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
                .orElseThrow(() -> new RuntimeException("No se encontro la sancion"));

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : sanction.getStartDate();
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : sanction.getEndDate();
        validateDates(startDate, endDate);

        if (request.getDescription() != null) sanction.setDescription(request.getDescription());
        if (request.getType() != null) sanction.setType(request.getType());
        if (request.getClasificacion() != null) sanction.setClasificacion(request.getClasificacion());
        if (request.getStartDate() != null) sanction.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) sanction.setEndDate(request.getEndDate());

        sanctionRepository.save(sanction);

        log.info("Sanction updated: {}", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Sancion actualizada correctamente");
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
                .orElseThrow(() -> new RuntimeException("No se encontro la sancion"));

        sanction.setState(false);
        sanctionRepository.save(sanction);

        log.info("Sanction deactivated: {}", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Sancion inactivada correctamente");
        return response;
    }

    /**
     * Devuelve las sanciones de un usuario con sus datos incluidos.
     */
    /**
     * Devuelve todas las sanciones con datos de usuario, filtradas por rango de fechas de inicio.
     */
    public List<SanctionWithUserDTO> getAllWithUserFiltered(LocalDate startDate, LocalDate endDate) {
        return getAllWithUserFiltered(null, startDate, endDate);
    }

    /**
     * Devuelve sanciones filtradas por usuario (opcional) y rango de fechas.
     */
    public List<SanctionWithUserDTO> getAllWithUserFiltered(Long userId, LocalDate startDate, LocalDate endDate) {
        return userSanctionRepository.findAll().stream()
                .filter(us -> {
                    if (userId != null && (us.getUser() == null || !us.getUser().getIdUser().equals(userId)))
                        return false;
                    Sanction s = us.getSanction();
                    if (startDate == null && endDate == null) return true;
                    if (s.getStartDate() == null) return false;
                    if (startDate != null && s.getStartDate().isBefore(startDate)) return false;
                    if (endDate != null && s.getStartDate().isAfter(endDate)) return false;
                    return true;
                })
                .sorted((a, b) -> {
                    LocalDate da = a.getSanction().getStartDate();
                    LocalDate db = b.getSanction().getStartDate();
                    if (da == null) return 1;
                    if (db == null) return -1;
                    return db.compareTo(da);
                })
                .map(us -> toDTO(us.getSanction(), us.getUser()))
                .collect(Collectors.toList());
    }

    public List<SanctionWithUserDTO> getByUserWithUser(Long userId) {
        if (userId == null) throw new RuntimeException("No se pudo identificar el usuario");
        return userSanctionRepository.findByUserIdUser(userId).stream()
                .map(us -> toDTO(us.getSanction(), us.getUser()))
                .collect(Collectors.toList());
    }

    private SanctionWithUserDTO toDTO(Sanction s, Users u) {
        SanctionWithUserDTO dto = new SanctionWithUserDTO();
        dto.setIdSanction(s.getIdSanction());
        dto.setDescription(s.getDescription());
        dto.setType(s.getType());
        dto.setStartDate(s.getStartDate());
        dto.setEndDate(s.getEndDate());
        dto.setState(s.getState());
        dto.setClasificacion(s.getClasificacion());
        if (u != null) {
            dto.setUserId(u.getIdUser());
            dto.setUserNames(u.getNames());
            dto.setUserSurnames(u.getSurnames());
            dto.setUserDoc(u.getDocNum());
            dto.setUserEmail(u.getEmail());
        }
        return dto;
    }

    private boolean isAdmin(Users user) {
        return user.getRole() != null
                && user.getRole().getName() != null
                && RoleEnum.ADMIN.getValue().equalsIgnoreCase(user.getRole().getName());
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new RuntimeException("Las fechas de la sancion son obligatorias");
        }
        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("La sancion debe iniciar y finalizar desde hoy en adelante");
        }
        if (endDate.isBefore(startDate)) {
            throw new RuntimeException("La fecha final no puede ser anterior a la fecha inicial");
        }
    }
}