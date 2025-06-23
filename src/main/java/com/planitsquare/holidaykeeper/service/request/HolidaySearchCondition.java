package com.planitsquare.holidaykeeper.service.request;

import com.planitsquare.holidaykeeper.domain.model.HolidayType;

public record HolidaySearchCondition(
        String countryCode,
        Integer fromYear,
        Integer toYear,
        Boolean global,
        HolidayType type
) {
}