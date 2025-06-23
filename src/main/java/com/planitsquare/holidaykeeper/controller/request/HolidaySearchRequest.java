package com.planitsquare.holidaykeeper.controller.request;

import com.planitsquare.holidaykeeper.domain.model.HolidayType;
import com.planitsquare.holidaykeeper.service.request.HolidaySearchCondition;

import java.time.Year;

public record HolidaySearchRequest(
        String country,           // ISO 코드 예: "KR"
        Integer fromYear,
        Integer toYear,             // 연도: 2024
        Boolean global,           // 글로벌 공휴일 여부
        HolidayType type          // Public, School, Optional 등
) {

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
