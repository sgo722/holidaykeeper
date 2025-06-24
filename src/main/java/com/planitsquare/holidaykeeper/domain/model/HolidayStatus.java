package com.planitsquare.holidaykeeper.domain.model;

import lombok.Getter;

@Getter
public enum HolidayStatus {
    ACTIVE("시행중"),
    ABOLIATION("폐지"),
    DELETE("삭제");

    private String name;

    HolidayStatus(String name) {
        this.name = name;
    }
}
