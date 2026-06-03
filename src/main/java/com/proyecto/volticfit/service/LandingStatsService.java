package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.Landing.LandingStatsDTO;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.repository.MachineRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LandingStatsService {

    private final UsersRepository usersRepository;
    private final MachineRepository machineRepository;

    public LandingStatsDTO getStats() {
        long activeMembers = usersRepository.countByStateTrueAndRoleNameNotIgnoreCase(RoleEnum.ADMIN.getValue());
        long availableEquipment = machineRepository.countByStateTrue();
        long experienceYears = calculateExperienceYears();

        return new LandingStatsDTO(activeMembers, availableEquipment, experienceYears);
    }

    private long calculateExperienceYears() {
        LocalDate firstMachineRegistration = machineRepository.findFirstRegistrationDate();
        if (firstMachineRegistration == null || firstMachineRegistration.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(firstMachineRegistration, LocalDate.now()).getYears();
    }
}
