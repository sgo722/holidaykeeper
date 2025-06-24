package com.planitsquare.holidaykeeper.domain.holiday.presentation.request;

import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidaySearchCondition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.Year;

public record HolidaySearchRequest(
        String country,
        @Min(1900) Integer fromYear,
        @Max(2100) Integer toYear,
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
