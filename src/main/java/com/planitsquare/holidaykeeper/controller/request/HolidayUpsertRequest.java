package com.planitsquare.holidaykeeper.controller.request;

import com.planitsquare.holidaykeeper.service.request.HolidayUpsertServiceRequest;

public record HolidayUpsertRequest(
        int year,
        String countryCode) {

    public HolidayUpsertServiceRequest toService() {
        return new HolidayUpsertServiceRequest(year, countryCode);
    }
}
