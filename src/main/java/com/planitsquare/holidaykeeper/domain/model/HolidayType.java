package com.planitsquare.holidaykeeper.domain.model;

public enum HolidayType {
    PUBLIC, BANK, SCHOOL, AUTHORITIES, OPTIONAL, OBSERVANCE;

    public static HolidayType from(String value) {
        return switch (value.toUpperCase()) {
            case "PUBLIC" -> PUBLIC;
            case "BANK" -> BANK;
            case "SCHOOL" -> SCHOOL;
            case "AUTHORITIES" -> AUTHORITIES;
            case "OPTIONAL" -> OPTIONAL;
            case "OBSERVANCE" -> OBSERVANCE; // 🔥 alias mapping
            default -> throw new IllegalArgumentException("Unknown type: " + value);
        };
    }
}
