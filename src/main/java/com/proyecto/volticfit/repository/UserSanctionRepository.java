package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.UserSanction;
import com.proyecto.volticfit.entity.UserSanctionId;

/**
 * Repositorio para la entidad UserSanction, que proporciona métodos para realizar operaciones CRUD en la base de datos relacionadas con las sanciones de los usuarios.
 */
public interface UserSanctionRepository extends JpaRepository<UserSanction, UserSanctionId> {

    /**
     * Encuentra las sanciones asociadas a un usuario específico.
     * @param idUser ID del usuario
     * @return Lista de sanciones asociadas al usuario dado
     */
    List<UserSanction> findByUserIdUser(Long idUser);
    
}
