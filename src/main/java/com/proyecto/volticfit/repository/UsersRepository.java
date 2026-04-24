package com.proyecto.volticfit.repository;
 
import java.util.List;
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.proyecto.volticfit.entity.Users;
 
/**
 * Repositorio para la entidad Users, proporciona métodos para acceder a los datos de usuarios en la base de datos.
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    /**
     * Find a user by their email.
     *
     * @param email the user email
     * @return an Optional containing the user if found
     */
    Optional<Users> findByEmail(String email);

    /**
     * Find all active users.
     *
     * @return list of active users
     */
    List<Users> findByStateTrue();

}