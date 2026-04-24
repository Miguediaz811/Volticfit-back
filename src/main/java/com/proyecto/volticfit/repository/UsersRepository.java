package com.proyecto.volticfit.repository;
 
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.proyecto.volticfit.entity.Users;
 
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByemail(String correo);
}