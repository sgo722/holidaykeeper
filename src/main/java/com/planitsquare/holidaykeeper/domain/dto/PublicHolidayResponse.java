package com.planitsquare.holidaykeeper.domain.dto;

import java.time.LocalDate;
import java.util.List;

public record PublicHolidayResponse(
        LocalDate date,
        String localName,
        String name,
        boolean fixed,
        boolean global,
        List<String> countyCodes,
        Integer launchYear,
        List<String> types
) {}