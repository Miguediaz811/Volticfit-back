package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.PhysicalEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing data operations and lifecycle of {@link PhysicalEvaluation} entities.
 */
@Repository
public interface PhysicalEvaluationRepository extends JpaRepository<PhysicalEvaluation, Long> { 

    /**
     * Retrieves all physical evaluation appointments associated with a specific student user.
     *
     * @param idUser the unique identifier of the student user
     * @return a list of physical evaluation records linked to the specified user
     */
    List<PhysicalEvaluation> findByUser_IdUser(Long idUser);
}