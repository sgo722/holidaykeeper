package com.planitsquare.holidaykeeper.domain.holiday.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record HolidayDeleteRequest(
    @Schema(description = "삭제할 연도", example = "2024", minimum = "1900", maximum = "2100")
    int year,
    @Schema(description = "국가코드(예: KR, US)", example = "KR")
    String countryCode
) {}
