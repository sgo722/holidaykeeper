package com.planitsquare.holidaykeeper.domain.holiday.presentation.request;

import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidaySearchCondition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.Year;

public record HolidaySearchRequest(
        String country,
        @Min(value = 1900, message = "시작 년도는 1900년 이상이어야 합니다.") Integer fromYear,
        @Max(value = 2100, message = "종료 연도는 2100년 이하여야 합니다.") Integer toYear,
        Boolean global,
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
