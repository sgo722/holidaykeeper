package com.planitsquare.holidaykeeper.domain.repository;

import com.planitsquare.holidaykeeper.domain.model.County;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountyRepository extends JpaRepository<County, Long> {
    List<County> findAllByCodeIn(List<String> counties);

    Optional<County> findByCode(String code);

    County findOrCreate(String countyCode);
}
