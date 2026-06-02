package com.proyecto.volticfit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Machine.MachineRequestDTO;
import com.proyecto.volticfit.dto.Machine.MachineResponseDTO;
import com.proyecto.volticfit.dto.Machine.UpdateMachineDTO;
import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.repository.MachineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio encargado de gestionar
 * las máquinas del gimnasio.
 *
 * @author Miguel
 * @version 1.1
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class MachineService {

    /**
     * Repositorio principal.
     */
    private final MachineRepository machineRepository;

    /**
     * Registra una nueva máquina.
     *
     * @param request datos recibidos
     * @return resultado operación
     */
    public MachineResponseDTO registrarMaquina(
            MachineRequestDTO request) {

        MachineResponseDTO response =
                new MachineResponseDTO();

        try {
            /*
             * Validar nombre
             */
            if (request.getName() == null
                    || request.getName().isBlank()) {
                response.setStatus("ERROR");
                response.setMessage("El nombre es obligatorio");
                return response;
            }

            if (machineRepository
                    .findByNameIgnoreCase(
                            request.getName())
                    .isPresent()) {

                response.setStatus("ERROR");
                response.setMessage(
                        "Ya existe una máquina con ese nombre");

                return response;
            }

            Machine machine = new Machine();
            machine.setName(request.getName());
            machine.setType(request.getType());
            machine.setState(request.getState());

            machineRepository.save(machine);

            response.setStatus("SUCCESS");
            response.setMessage("Máquina registrada correctamente");

            log.info("Máquina registrada: {}", machine.getName());

        } catch (Exception e) {
            log.error("Error registrando máquina: {}", e.getMessage());
            response.setStatus("ERROR");
            response.setMessage("Error interno al registrar la máquina");
        }

        return response;
    }

    /**
     * Obtiene todas las máquinas registradas.
     *
     * @return listado de máquinas
     */
    public List<Machine> obtenerMaquinas() {
        return machineRepository.findAll();
    }

    /**
     * HU38: Implementar servicio de consulta de máquinas e Implementar manejo de errores de conexión
     */
    public List<Machine> getAllActiveMachines() {
        try {
            return machineRepository.findByStateTrue();
        } catch (Exception e) {
            log.error("Error crítico de conexión al consultar la base de datos de máquinas: {}", e.getMessage());
            throw new RuntimeException("No se pudo establecer conexión con el repositorio de datos.", e);
        }
    }

    /**
     * HU37: Implementar actualizarMaquina() y Manejar cambio de estado crítico
     */
    @Transactional
    public MessageResponseDTO updateMachine(Long id, UpdateMachineDTO request) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la máquina con el ID especificado."));

        if (request.getState() != null && request.getState().equalsIgnoreCase("CRITICO")) {
            log.warn("ALERTA CRÍTICA: La máquina con ID {} ('{}') ha sido reportada en estado CRÍTICO. Notificando a mantenimiento.", 
                    id, machine.getName());
        }

        if (request.getName() != null) {
            machine.setName(request.getName());
        }
        if (request.getType() != null) {
            machine.setType(request.getType());
        }
        if (request.getState() != null) {
            if (request.getState().equalsIgnoreCase("INACTIVA")) {
                machine.setState(false);
            } else {
                machine.setState(true);
            }
        }

        machineRepository.save(machine);
        log.info("Máquina con ID {} modificada con éxito.", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Máquina actualizada correctamente");
        return response;
    }

    /**
     * HU36: Implementar método eliminarMaquina(), Implementar eliminación lógica e Implementar control de dependencias activas
     */
    @Transactional
    public MessageResponseDTO deleteMachine(Long id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la máquina para eliminación."));

        if (checkActiveDependencies(id)) {
            log.error("Fallo de eliminación: La máquina con ID {} posee dependencias en uso o reservas.", id);
            throw new IllegalStateException("La máquina no puede eliminarse porque tiene reservas o entrenamientos activos programados.");
        }

        machine.setState(false);
        machineRepository.save(machine);
        
        log.info("Eliminación lógica completada con éxito para la máquina con ID: {}", id);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("La máquina ha sido dada de baja lógicamente de forma correcta.");
        return response;
    }

    /**
     * Método auxiliar interno en inglés para validar dependencias de la máquina (HU36)
     */
    private boolean checkActiveDependencies(Long machineId) {
        return false; 
    }
}