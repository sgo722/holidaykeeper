package com.planitsquare.holidaykeeper.domain.holiday.business.request;

import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record HolidaySearchCondition(
        String countryCode,
        @Min(value = 1900, message = "시작 연도는 1900년 이상이어야 합니다.")
        @Max(value = 2100, message = "시작 연도는 2100년 이하여야 합니다.")
        Integer fromYear,
        @Min(value = 1900, message = "종료 연도는 1900년 이상이어야 합니다.")
        @Max(value = 2100, message = "종료 연도는 2100년 이하여야 합니다.")
        Integer toYear,
        Boolean global,
        HolidayType type
) {
    public HolidaySearchCondition {
        validateCondition(fromYear, toYear);
    }

    private static void validateCondition(Integer fromYear, Integer toYear) {
        validateYear(fromYear, toYear);
    }

    private static void validateYear(Integer fromYear, Integer toYear) {
        if (fromYear != null && toYear != null && fromYear > toYear) {
            throw new IllegalArgumentException("시작 연도는 종료 연도보다 클 수 없습니다.");
        }
    }
}