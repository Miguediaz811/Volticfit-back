package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing data operations and queries for {@link Machine} entities.
 */
@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    /**
     * Filters the machine inventory by its structural type and current operational state.
     *
     * @param type  the category of the machine (e.g., "Cardio", "Musculación")
     * @param state the operational status flag (true for active, false for maintenance)
     * @return a list of machines matching both criteria
     */
    List<Machine> findByTypeAndState(String type, Boolean state);

    /**
     * Retrieves a machine by its exact name, ignoring case sensitivity.
     *
     * @param name the name of the machine to search for
     * @return an Optional containing the found machine, or empty if no match exists
     */
    Optional<Machine> findByNameIgnoreCase(String name);
}