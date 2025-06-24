package com.planitsquare.holidaykeeper.domain.holiday.presentation.request;

public record HolidayDeleteRequest(
    int year,
    String countryCode
) {}
