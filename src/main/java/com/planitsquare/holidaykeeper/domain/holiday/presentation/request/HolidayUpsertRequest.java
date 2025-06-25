package com.planitsquare.holidaykeeper.domain.holiday.presentation.request;

import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidayUpsertServiceRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public record HolidayUpsertRequest(
        @Schema(description = "upsert할 연도", example = "2024", minimum = "1900", maximum = "2100")
        @Min(value = 1900, message = "최소 1900년 이상이어야 합니다.")
        @Max(value = 2100, message = "최대 2100년 이하여야 합니다.it ")
        int year,
        @Schema(description = "국가코드(예: KR, US)", example = "KR")
        @NotBlank String countryCode) {

    public HolidayUpsertServiceRequest toService() {
        return new HolidayUpsertServiceRequest(year, countryCode);
    }
}
