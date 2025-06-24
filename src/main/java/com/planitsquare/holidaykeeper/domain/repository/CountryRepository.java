package com.planitsquare.holidaykeeper.domain.repository;

import com.planitsquare.holidaykeeper.domain.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsByCode(String code);

    Country findByCode(String code);
}
