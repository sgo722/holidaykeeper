package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response;

import java.time.LocalDate;
import java.util.List;

public record HolidayNagerResponse(
        LocalDate date,
        String localName,
        String name,
        boolean fixed,
        boolean global,
        List<String> countyCodes,
        Integer launchYear,
        List<String> types
) {
}