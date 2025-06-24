package com.planitsquare.holidaykeeper.domain.holiday.business;

import com.planitsquare.holidaykeeper.domain.country.business.CountryService;
import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.domain.holiday.entity.County;
import com.planitsquare.holidaykeeper.domain.holiday.entity.DeleteHolidayCandidate;
import com.planitsquare.holidaykeeper.domain.holiday.entity.Holiday;
import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;
import com.planitsquare.holidaykeeper.domain.holiday.presentation.response.HolidayResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.PublicHolidayResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.CountyRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.HolidayQueryRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.HolidayRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.DeleteHolidayCandidateRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.NagerApiClient;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidaySearchCondition;
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
    private final CountyRepository countyRepository;
    private final DeleteHolidayCandidateRepository deleteHolidayCandidateRepository;
    private final CountryService countryService;

    @Transactional(readOnly = true)
    public Page<HolidayResponse> search(HolidaySearchCondition condition, Pageable pageable) {
        return holidayQueryRepository.search(condition, pageable)
                .map(HolidayResponse::from);
    }


    @Transactional
    public void upsertHolidays(int year, String countryCode) {
        Country findCountry = countryService.getCountryOrThrow(countryCode);

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
                deleteHolidayCandidateRepository.save(DeleteHolidayCandidate.from(oldData));
            }
        }
    }

    private County findOrCreateCounty(String code, Country country) {
        return countyRepository.findByCode(code)
                .orElseGet(() -> countyRepository.save(new County(code, country)));
    }

    @Transactional
    public int markHolidaysAsDeletedByYearAndCountry(int year, String countryCode) {
        Country country = countryService.getCountryOrThrow(countryCode);
        List<Holiday> holidays = holidayRepository.findByYearAndCountry(year, country);
        holidays.forEach(Holiday::markAsDeleted);
        return holidays.size();
    }
}
