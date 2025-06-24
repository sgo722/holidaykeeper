package com.planitsquare.holidaykeeper.service;

import com.planitsquare.holidaykeeper.controller.response.HolidayResponse;
import com.planitsquare.holidaykeeper.domain.dto.PublicHolidayResponse;
import com.planitsquare.holidaykeeper.domain.model.*;
import com.planitsquare.holidaykeeper.domain.repository.CountryRepository;
import com.planitsquare.holidaykeeper.domain.repository.CountyRepository;
import com.planitsquare.holidaykeeper.domain.repository.HolidayQueryRepository;
import com.planitsquare.holidaykeeper.domain.repository.HolidayRepository;
import com.planitsquare.holidaykeeper.domain.repository.DeleteCandidateRepository;
import com.planitsquare.holidaykeeper.infrastructure.api.NagerApiClient;
import com.planitsquare.holidaykeeper.service.request.HolidaySearchCondition;
import com.planitsquare.holidaykeeper.service.request.HolidayUpsertServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayQueryRepository holidayQueryRepository;
    private final NagerApiClient nagerApiClient;
    private final HolidayRepository holidayRepository;
    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final DeleteCandidateRepository deleteCandidateRepository;

    @Transactional(readOnly = true)
    public Page<HolidayResponse> search(HolidaySearchCondition condition, Pageable pageable) {
        return holidayQueryRepository.search(condition, pageable)
                .map(HolidayResponse::from);
    }


    @Transactional
    public void upsertHolidays(int year, String countryCode) {
        Country findCountry = countryRepository.findByCode(countryCode);
        if (findCountry == null) {
            throw new IllegalArgumentException("[ERROR] 해당 나라코드 지원하지 않습니다");
        }

        List<Holiday> existing = holidayRepository.findByYearAndCountry(year, findCountry);
        List<PublicHolidayResponse> fetched = nagerApiClient.getPublicHolidays(countryCode, year);

        for (PublicHolidayResponse newData : fetched) {
            Optional<Holiday> maybeHoliday = existing.stream()
                    .filter(e -> e.getDate().equals(newData.date()))
                    .findFirst();

            List<County> counties = Optional.ofNullable(newData.countyCodes())
                .orElse(List.of())
                .stream()
                .map(code -> findOrCreateCounty(code, findCountry))
                .collect(Collectors.toList());

            Set<HolidayType> updateTypes = newData.types().stream()
                .map(HolidayType::from)
                .collect(Collectors.toSet());

            if (maybeHoliday.isPresent()) {
                Holiday holiday = maybeHoliday.get();
                boolean changed = holiday.updateIfChanged(
                    newData.name(),
                    newData.localName(),
                    newData.global(),
                    updateTypes,
                    counties
                );
                if (changed) {
                    holidayRepository.save(holiday);
                }
            } else {
                Holiday newHoliday = Holiday.of(
                    findCountry,
                    newData.date(),
                    newData.localName(),
                    newData.name(),
                    newData.fixed(),
                    newData.global(),
                    newData.launchYear(),
                    updateTypes,
                    counties
                );
                holidayRepository.save(newHoliday);
            }
        }

        Set<LocalDate> apiDates = fetched.stream()
                .map(PublicHolidayResponse::date)
                .collect(Collectors.toSet());

        for (Holiday oldData : existing) {
            if (!apiDates.contains(oldData.getDate())) {
                deleteCandidateRepository.save(DeleteCandidate.from(oldData));
            }
        }
    }

    private County findOrCreateCounty(String code, Country country) {
        return countyRepository.findByCode(code)
                .orElseGet(() -> countyRepository.save(new County(code, country)));
    }
}
