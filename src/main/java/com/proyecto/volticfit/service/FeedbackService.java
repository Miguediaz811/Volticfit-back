package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Feedback.FeedbackRequestDTO;
import com.proyecto.volticfit.entity.Feedback;
import com.proyecto.volticfit.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository repository;

    public Feedback registerFeedback(Long userId, FeedbackRequestDTO dto) {
        Feedback feedback = new Feedback();
        feedback.setUsuarioId(userId);
        feedback.setCategoria(dto.getCategoria());
        feedback.setDescripcion(dto.getDescripcion());
        return repository.save(feedback);
    }

    public List<Feedback> getAllFeedbacks() {
        return repository.findAll();
    }
}