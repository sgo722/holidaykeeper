package com.planitsquare.holidaykeeper.domain.holiday.presentation.request;

import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidaySearchCondition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Year;

public record HolidaySearchRequest(
        @Schema(description = "국가코드(예: KR, US)", example = "KR")
        String country,
        @Schema(description = "검색 시작 연도", example = "2024", minimum = "1900", maximum = "2100")
        @Min(value = 1900, message = "시작 년도는 1900년 이상이어야 합니다.") Integer fromYear,
        @Schema(description = "검색 종료 연도", example = "2024", minimum = "1900", maximum = "2100")
        @Max(value = 2100, message = "종료 연도는 2100년 이하여야 합니다.") Integer toYear,
        @Schema(description = "전역 공휴일 여부", example = "true")
        Boolean global,
        @Schema(description = "공휴일 타입(PUBLIC, BANK, SCHOOL, AUTHORITIES, OPTIONAL, OBSERVANCE)", example = "PUBLIC")
        HolidayType type) {

    public HolidaySearchCondition toCondition() {
        return new HolidaySearchCondition(
                this.country(),
                this.fromYear() != null ? this.fromYear() : Year.now().getValue(),
                this.toYear() != null ? this.toYear() : Year.now().getValue(),
                this.global(),
                this.type()
        );
    }
}
