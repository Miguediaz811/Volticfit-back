package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}