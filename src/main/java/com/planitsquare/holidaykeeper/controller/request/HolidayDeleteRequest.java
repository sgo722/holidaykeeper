package com.planitsquare.holidaykeeper.controller.request;

public record HolidayDeleteRequest(
    int year,
    String countryCode
) {}
