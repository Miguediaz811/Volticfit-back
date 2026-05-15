package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Metricas.MetricaRequestDTO;
import com.proyecto.volticfit.dto.Metricas.MetricaResponseDTO;
import com.proyecto.volticfit.entity.Metrica;
import com.proyecto.volticfit.repository.MetricaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricaService {

    private final MetricaRepository metricaRepository;

    // ── Registrar métricas ────────────────────────────────────────────────────
    public MetricaResponseDTO registrarMetrica(MetricaRequestDTO request, String correoUsuario) {
        double imc = calcularImc(request.getPesoKg(), request.getEstaturaCm());

        Metrica metrica = new Metrica();
        metrica.setCorreoUsuario(correoUsuario);
        metrica.setPesoKg(request.getPesoKg());
        metrica.setEstaturaCm(request.getEstaturaCm());
        metrica.setImc(imc);
        metrica.setPorcentajeGrasa(request.getPorcentajeGrasa());
        metrica.setCinturaCm(request.getCinturaCm());
        metrica.setCaderaCm(request.getCaderaCm());
        metrica.setPechoCm(request.getPechoCm());
        metrica.setBrazoCm(request.getBrazoCm());
        metrica.setFechaRegistro(LocalDateTime.now());

        metricaRepository.save(metrica);
        return buildResponse(metrica);
    }

    // ── Consultar historial de métricas del usuario ───────────────────────────
    public List<MetricaResponseDTO> consultarMetricas(String correoUsuario) {
        return metricaRepository.findByCorreoUsuarioOrderByFechaRegistroDesc(correoUsuario)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    // ── Validar existencia de datos (HU18 - criterio validar existencia) ──────
    public boolean tieneMetricas(String correoUsuario) {
        return metricaRepository.existsByCorreoUsuario(correoUsuario);
    }

    // ── IMC: peso(kg) / (estatura(m))^2 ──────────────────────────────────────
    private double calcularImc(double pesoKg, double estaturaCm) {
        double estaturaM = estaturaCm / 100.0;
        double imc = pesoKg / (estaturaM * estaturaM);
        // Redondear a 2 decimales
        return Math.round(imc * 100.0) / 100.0;
    }

    // ── Categoría IMC según OMS ───────────────────────────────────────────────
    private String categoriaImc(double imc) {
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25.0) return "Normal";
        if (imc < 30.0) return "Sobrepeso";
        return "Obesidad";
    }

    private MetricaResponseDTO buildResponse(Metrica m) {
        MetricaResponseDTO dto = new MetricaResponseDTO();
        dto.setIdMetrica(m.getIdMetrica());
        dto.setCorreoUsuario(m.getCorreoUsuario());
        dto.setPesoKg(m.getPesoKg());
        dto.setEstaturaCm(m.getEstaturaCm());
        dto.setImc(m.getImc());
        dto.setCategoriaImc(categoriaImc(m.getImc()));
        dto.setPorcentajeGrasa(m.getPorcentajeGrasa());
        dto.setCinturaCm(m.getCinturaCm());
        dto.setCaderaCm(m.getCaderaCm());
        dto.setPechoCm(m.getPechoCm());
        dto.setBrazoCm(m.getBrazoCm());
        dto.setFechaRegistro(m.getFechaRegistro());
        return dto;
    }
}