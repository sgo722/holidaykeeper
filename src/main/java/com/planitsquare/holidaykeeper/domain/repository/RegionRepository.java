package com.planitsquare.holidaykeeper.domain.repository;

import com.planitsquare.holidaykeeper.domain.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findAllByCodeIn(List<String> counties);

    Optional<Region> findByCode(String code);
}
