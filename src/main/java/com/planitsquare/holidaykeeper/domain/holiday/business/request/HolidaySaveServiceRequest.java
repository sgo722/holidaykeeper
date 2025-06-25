package com.planitsquare.holidaykeeper.domain.holiday.business.request;

import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record HolidaySaveServiceRequest(
        Country country,
        LocalDate date,
        String localName,
        String name,
        boolean fixed,
        boolean global,
        Integer launchYear,
        Set<HolidayType> types,
        List<String> countyCodes
) {}