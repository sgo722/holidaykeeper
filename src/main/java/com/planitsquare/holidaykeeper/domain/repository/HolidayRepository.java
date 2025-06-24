package com.planitsquare.holidaykeeper.domain.repository;

import com.planitsquare.holidaykeeper.domain.model.Country;
import com.planitsquare.holidaykeeper.domain.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsByDateBetween(LocalDate from, LocalDate to);

    boolean existsByCountryAndDate(Country country, LocalDate date);

    @Query(value = "SELECT * FROM holiday WHERE YEAR(date) = :year AND country_id = :#{#country.id}", nativeQuery = true)
    List<Holiday> findByYearAndCountry(@Param("year") int year, @Param("country") Country country);
}
