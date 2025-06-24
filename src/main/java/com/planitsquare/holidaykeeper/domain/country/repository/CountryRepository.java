package com.planitsquare.holidaykeeper.domain.country.repository;

import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsByCode(String code);

    Country findByCode(String countryCode);
}
