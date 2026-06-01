package com.proyecto.volticfit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.Role;

/**
 * Repositorio para la entidad Role, que proporciona métodos para realizar operaciones CRUD en la base de datos relacionadas con los roles.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Find a role by its name.
     *
     * @param name the role name
     * @return an Optional containing the role if found
     */
    Optional<Role> findByName(String name);
}
