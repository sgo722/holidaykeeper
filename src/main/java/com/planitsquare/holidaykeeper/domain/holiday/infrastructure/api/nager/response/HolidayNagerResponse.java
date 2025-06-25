package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response;

import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidaySaveServiceRequest;
import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record HolidayNagerResponse(
        LocalDate date,
        String localName,
        String name,
        boolean fixed,
        boolean global,
        List<String> countyCodes,
        Integer launchYear,
        List<String> types
) {
    public HolidaySaveServiceRequest toService(Country country) {
        Set<HolidayType> typeEnums = this.types() != null
            ? this.types().stream().map(HolidayType::from).collect(Collectors.toSet())
            : Set.of();
        return new HolidaySaveServiceRequest(
            country,
            this.date(),
            this.localName(),
            this.name(),
            this.fixed(),
            this.global(),
            this.launchYear(),
            typeEnums,
            this.countyCodes() != null ? this.countyCodes() : List.of()
        );
    }
}