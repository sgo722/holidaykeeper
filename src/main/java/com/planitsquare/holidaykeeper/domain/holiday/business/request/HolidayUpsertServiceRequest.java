package com.planitsquare.holidaykeeper.domain.holiday.business.request;

public record HolidayUpsertServiceRequest(
        int year,
        String countryCode) {
}
