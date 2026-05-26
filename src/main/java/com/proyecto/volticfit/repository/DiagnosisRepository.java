package com.proyecto.volticfit.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.volticfit.entity.Diagnosis;

/**
 * Repository for Diagnosis entity.
 */
@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long>{
     
    /**
     * Find all diagnoses for a specific user.
     *
     * @param userId the user ID
     * @return list of diagnoses
     */
    List<Diagnosis> findByUserIdUser(Long userId);
}
