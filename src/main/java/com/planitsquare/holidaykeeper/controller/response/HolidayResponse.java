package com.planitsquare.holidaykeeper.controller.response;

import com.planitsquare.holidaykeeper.domain.model.Holiday;
import com.planitsquare.holidaykeeper.domain.model.HolidayType;

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
            holiday.getRegions().stream()
                    .map(region -> region.getRegion().getCode())
                    .toList()
        );
    }
}