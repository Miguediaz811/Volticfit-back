package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.UserSanction;
import com.proyecto.volticfit.entity.UserSanctionId;

public interface UserSanctionRepository extends JpaRepository<UserSanction, UserSanctionId> {

    List<UserSanction> findByUserIdUser(Long idUser);
    
}
