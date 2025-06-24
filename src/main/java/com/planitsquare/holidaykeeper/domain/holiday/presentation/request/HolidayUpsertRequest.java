package com.planitsquare.holidaykeeper.domain.holiday.presentation.request;

import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidayUpsertServiceRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record HolidayUpsertRequest(
        @Min(1900) @Max(2100) int year,
        @NotBlank String countryCode) {

    public HolidayUpsertServiceRequest toService() {
        return new HolidayUpsertServiceRequest(year, countryCode);
    }
}
