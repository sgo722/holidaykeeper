package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository;

import com.planitsquare.holidaykeeper.domain.holiday.entity.County;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountyRepository extends JpaRepository<County, Long> {
    Optional<County> findByCode(String code);
}
