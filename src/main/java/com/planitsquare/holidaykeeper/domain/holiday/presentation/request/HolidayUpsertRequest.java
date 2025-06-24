package com.planitsquare.holidaykeeper.domain.holiday.presentation.request;

import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidayUpsertServiceRequest;

public record HolidayUpsertRequest(
        int year,
        String countryCode) {

    public HolidayUpsertServiceRequest toService() {
        return new HolidayUpsertServiceRequest(year, countryCode);
    }
}
