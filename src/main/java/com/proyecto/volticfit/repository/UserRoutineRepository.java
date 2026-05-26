package com.proyecto.volticfit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.UserRoutine;
import com.proyecto.volticfit.entity.UserRoutineId;

@Repository
public interface UserRoutineRepository extends JpaRepository<UserRoutine, UserRoutineId> {

    List<UserRoutine> findByUserIdUser(Long userId);
    Optional<UserRoutine> findByUserIdUserAndActiveTrue(Long userId);
    List<UserRoutine> findByUserIdUserAndActive(Long userId, Boolean active);
}
