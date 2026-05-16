package com.proyecto.volticfit.service;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.volticfit.dto.Rutinas.EjercicioDTO;
import com.proyecto.volticfit.dto.Rutinas.EjercicioResponseDTO;
import com.proyecto.volticfit.dto.Rutinas.RestaurarVersionRequestDTO;
import com.proyecto.volticfit.dto.Rutinas.RutinaRequestDTO;
import com.proyecto.volticfit.dto.Rutinas.RutinaResponseDTO;
import com.proyecto.volticfit.dto.Rutinas.RutinaVersionResponseDTO;
import com.proyecto.volticfit.entity.Ejercicio;
import com.proyecto.volticfit.entity.Rutina;
import com.proyecto.volticfit.entity.RutinaVersion;
import com.proyecto.volticfit.repository.EjercicioRepository;
import com.proyecto.volticfit.repository.RutinaRepository;
import com.proyecto.volticfit.repository.RutinaVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final EjercicioRepository ejercicioRepository;
    private final RutinaVersionRepository versionRepository;
    private final ObjectMapper objectMapper;

    // ── Crear rutina ──────────────────────────────────────────────────────────
    @Transactional
    public RutinaResponseDTO crearRutina(RutinaRequestDTO request, String correoUsuario) {
        Rutina rutina = new Rutina();
        rutina.setNombre(request.getNombre());
        rutina.setDescripcion(request.getDescripcion());
        rutina.setCorreoUsuario(correoUsuario);
        rutina.setFechaCreacion(LocalDateTime.now());
        rutina.setVersionActual(1);
        rutinaRepository.save(rutina);

        List<Ejercicio> ejercicios = guardarEjercicios(request.getEjercicios(), rutina);

        // Snapshot versión 1
        guardarSnapshot(rutina, ejercicios, correoUsuario, false, null);

        return buildRutinaResponse(rutina, ejercicios);
    }

    // ── Actualizar rutina (genera nueva versión) ───────────────────────────────
    @Transactional
    public RutinaResponseDTO actualizarRutina(Long idRutina, RutinaRequestDTO request, String correoUsuario) {
        Rutina rutina = rutinaRepository.findByIdRutinaAndEstadoTrue(idRutina)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // Snapshot de la versión ACTUAL antes de modificar
        List<Ejercicio> ejerciciosActuales = ejercicioRepository.findByRutina_IdRutina(idRutina);
        guardarSnapshot(rutina, ejerciciosActuales, correoUsuario, false, null);

        // Actualizar rutina
        rutina.setNombre(request.getNombre());
        rutina.setDescripcion(request.getDescripcion());
        rutina.setFechaModificacion(LocalDateTime.now());
        rutina.setModificadoPor(correoUsuario);
        rutina.setVersionActual(rutina.getVersionActual() + 1);
        rutinaRepository.save(rutina);

        // Reemplazar ejercicios
        ejercicioRepository.deleteByRutina_IdRutina(idRutina);
        List<Ejercicio> nuevosEjercicios = guardarEjercicios(request.getEjercicios(), rutina);

        return buildRutinaResponse(rutina, nuevosEjercicios);
    }

    // ── Consultar historial de versiones ──────────────────────────────────────
    public List<RutinaVersionResponseDTO> consultarHistorial(Long idRutina, String correoUsuario) {
        Rutina rutina = rutinaRepository.findByIdRutinaAndEstadoTrue(idRutina)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        if (!rutina.getCorreoUsuario().equals(correoUsuario)) {
            throw new RuntimeException("No tienes acceso a esta rutina");
        }

        return versionRepository.findByIdRutinaOrderByNumeroVersionDesc(idRutina)
                .stream()
                .map(this::buildVersionResponse)
                .toList();
    }

    // ── Restaurar una versión anterior ────────────────────────────────────────
    @Transactional
    public RutinaResponseDTO restaurarVersion(Long idRutina, RestaurarVersionRequestDTO request, String correoUsuario) {
        Rutina rutina = rutinaRepository.findByIdRutinaAndEstadoTrue(idRutina)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        if (!rutina.getCorreoUsuario().equals(correoUsuario)) {
            throw new RuntimeException("No tienes acceso a esta rutina");
        }

        RutinaVersion versionObjetivo = versionRepository
                .findByIdRutinaAndNumeroVersion(idRutina, request.getNumeroVersion())
                .orElseThrow(() -> new RuntimeException("Versión no encontrada"));

        // Snapshot del estado actual antes de restaurar
        List<Ejercicio> ejerciciosActuales = ejercicioRepository.findByRutina_IdRutina(idRutina);
        guardarSnapshot(rutina, ejerciciosActuales, correoUsuario, false, null);

        // Restaurar datos de la rutina desde el snapshot
        rutina.setNombre(versionObjetivo.getNombreRutina());
        rutina.setDescripcion(versionObjetivo.getDescripcionRutina());
        rutina.setFechaModificacion(LocalDateTime.now());
        rutina.setModificadoPor(correoUsuario);
        rutina.setVersionActual(rutina.getVersionActual() + 1);
        rutinaRepository.save(rutina);

        // Restaurar ejercicios desde el JSON del snapshot
        ejercicioRepository.deleteByRutina_IdRutina(idRutina);
        List<EjercicioDTO> ejerciciosDTOs = deserializarEjercicios(versionObjetivo.getEjerciciosJson());
        List<Ejercicio> ejerciciosRestaurados = guardarEjercicios(ejerciciosDTOs, rutina);

        // Registrar la restauración como nueva versión en historial
        guardarSnapshot(rutina, ejerciciosRestaurados, correoUsuario, true, request.getNumeroVersion());

        return buildRutinaResponse(rutina, ejerciciosRestaurados);
    }

    // ── Listar rutinas del usuario ─────────────────────────────────────────────
    public List<RutinaResponseDTO> listarRutinas(String correoUsuario) {
        return rutinaRepository.findByCorreoUsuarioAndEstadoTrue(correoUsuario)
                .stream()
                .map(r -> {
                    List<Ejercicio> ejercicios = ejercicioRepository.findByRutina_IdRutina(r.getIdRutina());
                    return buildRutinaResponse(r, ejercicios);
                })
                .toList();
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private List<Ejercicio> guardarEjercicios(List<EjercicioDTO> dtos, Rutina rutina) {
        return dtos.stream().map(dto -> {
            Ejercicio e = new Ejercicio();
            e.setRutina(rutina);
            e.setNombre(dto.getNombre());
            e.setSeries(dto.getSeries());
            e.setRepeticiones(dto.getRepeticiones());
            e.setPesoKg(dto.getPesoKg());
            e.setNotas(dto.getNotas());
            return ejercicioRepository.save(e);
        }).toList();
    }

    private void guardarSnapshot(Rutina rutina, List<Ejercicio> ejercicios,
                                  String guardadoPor, boolean esRestauracion,
                                  Integer versionOrigen) {
        Integer numeroVersion = versionRepository.countByIdRutina(rutina.getIdRutina()) + 1;

        RutinaVersion version = new RutinaVersion();
        version.setIdRutina(rutina.getIdRutina());
        version.setNumeroVersion(numeroVersion);
        version.setNombreRutina(rutina.getNombre());
        version.setDescripcionRutina(rutina.getDescripcion());
        version.setEjerciciosJson(serializarEjercicios(ejercicios));
        version.setFechaGuardado(LocalDateTime.now());
        version.setGuardadoPor(guardadoPor);
        version.setEsRestauracion(esRestauracion);
        version.setVersionOrigenRestauracion(versionOrigen);
        versionRepository.save(version);
    }

    private String serializarEjercicios(List<Ejercicio> ejercicios) {
        try {
            List<EjercicioDTO> dtos = ejercicios.stream().map(e -> {
                EjercicioDTO dto = new EjercicioDTO();
                dto.setNombre(e.getNombre());
                dto.setSeries(e.getSeries());
                dto.setRepeticiones(e.getRepeticiones());
                dto.setPesoKg(e.getPesoKg());
                dto.setNotas(e.getNotas());
                return dto;
            }).toList();
            return objectMapper.writeValueAsString(dtos);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Error al serializar ejercicios");
        }
    }

    private List<EjercicioDTO> deserializarEjercicios(String json) {
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, EjercicioDTO.class));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Error al deserializar ejercicios del historial");
        }
    }

    private RutinaResponseDTO buildRutinaResponse(Rutina rutina, List<Ejercicio> ejercicios) {
        RutinaResponseDTO dto = new RutinaResponseDTO();
        dto.setIdRutina(rutina.getIdRutina());
        dto.setNombre(rutina.getNombre());
        dto.setDescripcion(rutina.getDescripcion());
        dto.setCorreoUsuario(rutina.getCorreoUsuario());
        dto.setFechaCreacion(rutina.getFechaCreacion());
        dto.setFechaModificacion(rutina.getFechaModificacion());
        dto.setModificadoPor(rutina.getModificadoPor());
        dto.setVersionActual(rutina.getVersionActual());
        dto.setEjercicios(ejercicios.stream().map(e -> {
            EjercicioResponseDTO er = new EjercicioResponseDTO();
            er.setIdEjercicio(e.getIdEjercicio());
            er.setNombre(e.getNombre());
            er.setSeries(e.getSeries());
            er.setRepeticiones(e.getRepeticiones());
            er.setPesoKg(e.getPesoKg());
            er.setNotas(e.getNotas());
            return er;
        }).toList());
        return dto;
    }

    private RutinaVersionResponseDTO buildVersionResponse(RutinaVersion v) {
        RutinaVersionResponseDTO dto = new RutinaVersionResponseDTO();
        dto.setIdVersion(v.getIdVersion());
        dto.setNumeroVersion(v.getNumeroVersion());
        dto.setNombreRutina(v.getNombreRutina());
        dto.setDescripcionRutina(v.getDescripcionRutina());
        dto.setEjerciciosJson(v.getEjerciciosJson());
        dto.setFechaGuardado(v.getFechaGuardado());
        dto.setGuardadoPor(v.getGuardadoPor());
        dto.setEsRestauracion(v.getEsRestauracion());
        dto.setVersionOrigenRestauracion(v.getVersionOrigenRestauracion());
        return dto;
    }
}