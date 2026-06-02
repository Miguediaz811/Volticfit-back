package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.CalendarToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for managing data operations and lifecycle of {@link CalendarToken} entities.
 */
@Repository
public interface CalendarTokenRepository extends JpaRepository<CalendarToken, Integer> {

    /**
     * Retrieves an active calendar token linked to a specific user and integration provider.
     *
     * @param idUser   the unique identifier of the user
     * @param provider the integration service provider name (e.g., "google")
     * @return an Optional containing the found token, or empty if no match exists
     */
    Optional<CalendarToken> findByUser_IdUserAndProvider(Long idUser, String provider);

    /**
     * Retrieves any available calendar token linked to a specific user identifier.
     *
     * @param idUser the unique identifier of the user
     * @return an Optional containing the found token, or empty if no match exists
     */
    Optional<CalendarToken> findByUser_IdUser(Long idUser);
}