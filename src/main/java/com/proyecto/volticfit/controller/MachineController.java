package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.Machine.MachineRequestDTO;
import com.proyecto.volticfit.dto.Machine.MachineResponseDTO;
import com.proyecto.volticfit.dto.Machine.UpdateMachineDTO;
import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.service.MachineService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador encargado
 * de la gestión de máquinas.
 *
 * @author Miguel
 * @version 1.0
 */
@RestController
@RequestMapping("/api/maquinas")
@RequiredArgsConstructor
public class MachineController {

    /**
     * Servicio principal.
     */
    private final MachineService machineService;

    /**
     * Registra una máquina.
     *
     * @param request datos máquina
     * @return resultado operación
     */
    @PostMapping
    public MachineResponseDTO registrar(
            @RequestBody
            MachineRequestDTO request) {

        return machineService
                .registrarMaquina(request);
    }

    /**
     * Consulta todas las máquinas.
     *
     * @return listado de máquinas
     */
    @GetMapping
    public List<Machine> listar() {

        return machineService
                .obtenerMaquinas();
    }

    @PutMapping("/{id}")
    public MachineResponseDTO actualizar(
            @PathVariable Long id,
            @RequestBody UpdateMachineDTO request) {

        return machineService.actualizarMaquina(id, request);
    }

    @PutMapping("/{id}/inactivar")
    public MachineResponseDTO inactivar(@PathVariable Long id) {
        return machineService.inactivarMaquina(id);
    }

    @DeleteMapping("/{id}")
    public MachineResponseDTO eliminar(@PathVariable Long id) {
        return machineService.eliminarMaquina(id);
    }
}