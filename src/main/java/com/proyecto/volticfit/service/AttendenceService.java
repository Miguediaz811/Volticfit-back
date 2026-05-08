package com.proyecto.volticfit.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.AttendenceResponseDTO;
import com.proyecto.volticfit.entity.Attendence;
import com.proyecto.volticfit.repository.AttendenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendenceService {

    private final AttendenceRepository attendenceRepository;
    private final JwtService jwtService;

    /**
     * Consulta asistencias del usuario autenticado sin auditoría.
     *
     * @param token JWT del usuario
     * @param page número de página
     * @param size tamaño de página
     * @return lista paginada de asistencias
     */
    public Page<AttendenceResponseDTO> getAttendences(String token, int page, int size) {

       
        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Token inválido");
        }

        String email = jwtService.extractEmail(token);

       
        String docNum = email;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        Page<Attendence> result =
                attendenceRepository.findByDocNum(docNum, pageable);

        
        if (result.isEmpty()) {
            return Page.empty();
        }

        
        return result.map(att ->
                new AttendenceResponseDTO(
                        att.getNames() + " " + att.getSurnames(),
                        att.getDocNum()
                )
        );
    }
}