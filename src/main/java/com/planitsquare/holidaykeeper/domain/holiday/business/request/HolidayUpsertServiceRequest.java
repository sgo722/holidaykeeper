package com.planitsquare.holidaykeeper.domain.holiday.business.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record HolidayUpsertServiceRequest(
        @Min(value = 1900, message = "연도는 1900년 이상이어야 합니다.")
        @Max(value = 2100, message = "연도는 2100년 이하여야 합니다.")
        int year,
        @NotBlank(message = "국가코드는 필수입니다.")
        String countryCode) {
}
