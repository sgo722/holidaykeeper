package com.planitsquare.holidaykeeper.domain.holiday.presentation.response;

import com.planitsquare.holidaykeeper.domain.holiday.entity.Holiday;
import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record HolidayResponse(
        @Schema(description = "국가코드", example = "KR")
        String countryCode,
        @Schema(description = "공휴일 이름(한글)", example = "신정")
        String name,
        @Schema(description = "공휴일 이름(영문)", example = "New Year's Day")
        String localName,
        @Schema(description = "날짜", example = "2024-01-01")
        LocalDate date,
        @Schema(description = "고정일 여부", example = "true")
        Boolean fixed,
        @Schema(description = "전역 공휴일 여부", example = "true")
        Boolean global,
        @Schema(description = "시행 연도", example = "1949")
        Integer launchYear,
        @Schema(description = "공휴일 타입 목록", example = "[\"PUBLIC\"]")
        List<HolidayType> types,
        @Schema(description = "지역 코드 목록", example = "[\"11\", \"26\"]")
        List<String> regionCodes
) {
    public static HolidayResponse from(Holiday holiday) {
        return new HolidayResponse(
            holiday.getCountryCode(),
            holiday.getName(),
            holiday.getLocalName(),
            holiday.getDate(),
            holiday.isFixed(),
            holiday.isGlobal(),
            holiday.getLaunchYear(),
            List.copyOf(holiday.getTypes()),
            holiday.getCounties().stream()
                    .map(region -> region.getCounty().getCode())
                    .toList()
        );
    }
}