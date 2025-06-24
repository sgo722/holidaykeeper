package com.planitsquare.holidaykeeper.service.request;

public record HolidayUpsertServiceRequest(
        int year,
        String countryCode) {
}
