package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing data operations and lifecycle of {@link Recommendation} entities.
 */
@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {
    
    /**
     * Retrieves all personalized recommendations issued for a specific user, 
     * ordered chronologically by creation date in descending order.
     *
     * @param idUser the unique identifier of the user
     * @return a list of recommendations associated with the specified user, sorted from newest to oldest
     */
    List<Recommendation> findByUser_IdUserOrderByCreationDateDesc(Long idUser);
}