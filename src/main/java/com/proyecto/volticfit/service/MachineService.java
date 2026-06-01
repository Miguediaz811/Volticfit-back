package com.proyecto.volticfit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.Machine.MachineRequestDTO;
import com.proyecto.volticfit.dto.Machine.MachineResponseDTO;
import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.repository.MachineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio encargado de gestionar
 * las máquinas del gimnasio.
 *
 * @author Miguel
 * @version 1.0
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
                response.setMessage(
                        "El nombre es obligatorio");

                return response;
            }

            /*
             * Validar tipo
             */
            if (request.getType() == null
                    || request.getType().isBlank()) {

                response.setStatus("ERROR");
                response.setMessage(
                        "El tipo es obligatorio");

                return response;
            }

            /*
             * Validar estado
             */
            if (request.getState() == null) {

                response.setStatus("ERROR");
                response.setMessage(
                        "El estado es obligatorio");

                return response;
            }

            /*
             * Evitar duplicados
             */
            if (machineRepository
                    .findByName(
                            request.getName())
                    .isPresent()) {

                response.setStatus("ERROR");
                response.setMessage(
                        "Ya existe una máquina con ese nombre");

                return response;
            }

            Machine machine =
                    new Machine();

            machine.setName(
                    request.getName());

            machine.setType(
                    request.getType());

            machine.setState(
                    request.getState());



            machineRepository.save(machine);

            response.setStatus("SUCCESS");
            response.setMessage(
                    "Máquina registrada correctamente");

            log.info(
                    "Máquina registrada: {}",
                    machine.getName());

        } catch (Exception e) {

            log.error(
                    "Error registrando máquina: {}",
                    e.getMessage());

            response.setStatus("ERROR");
            response.setMessage(
                    "Error interno al registrar la máquina");
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
}