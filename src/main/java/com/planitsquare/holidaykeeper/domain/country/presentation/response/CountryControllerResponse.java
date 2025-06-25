package com.planitsquare.holidaykeeper.domain.country.presentation.response;

import com.planitsquare.holidaykeeper.domain.country.business.response.CountryViewServiceResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record CountryControllerResponse(
    @Schema(description = "국가 ID", example = "1")
    Long id,
    @Schema(description = "국가코드", example = "KR")
    String code,
    @Schema(description = "국가명", example = "대한민국")
    String name
) {
    public static CountryControllerResponse from(CountryViewServiceResponse serviceResponse) {
        return new CountryControllerResponse(serviceResponse.id(), serviceResponse.code(), serviceResponse.name());
    }
}
