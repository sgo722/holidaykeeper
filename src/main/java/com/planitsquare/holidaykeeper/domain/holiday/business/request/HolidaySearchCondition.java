package com.planitsquare.holidaykeeper.domain.holiday.business.request;

import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;

public record HolidaySearchCondition(
        String countryCode,
        Integer fromYear,
        Integer toYear,
        Boolean global,
        HolidayType type
) {
}