package com.planitsquare.holidaykeeper.domain.holiday.presentation.response;

import com.planitsquare.holidaykeeper.domain.holiday.entity.Holiday;
import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;

import java.time.LocalDate;
import java.util.List;

public record HolidayResponse(
        String countryCode,
        String name,
        String localName,
        LocalDate date,
        Boolean fixed,
        Boolean global,
        Integer launchYear,
        List<HolidayType> types,
        List<String> regionCodes // 지역코드 목록
) {
    public static HolidayResponse from(Holiday holiday) {
        return new HolidayResponse(
            holiday.getCountryCode(),
            holiday.getName(),
            holiday.getLocalName(),
            holiday.getDate(),
            holiday.isFixed(),
            holiday.isGlobal(),
            holiday.getLaunchYear(),
            List.copyOf(holiday.getTypes()),
            holiday.getCounties().stream()
                    .map(region -> region.getCounty().getCode())
                    .toList()
        );
    }
}