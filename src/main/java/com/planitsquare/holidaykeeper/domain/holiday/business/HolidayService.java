package com.planitsquare.holidaykeeper.domain.holiday.business;

import com.planitsquare.holidaykeeper.domain.country.business.CountryService;
import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidaySaveServiceRequest;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidaySearchCondition;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidayUpsertServiceRequest;
import com.planitsquare.holidaykeeper.domain.holiday.entity.County;
import com.planitsquare.holidaykeeper.domain.holiday.entity.DeleteHolidayCandidate;
import com.planitsquare.holidaykeeper.domain.holiday.entity.Holiday;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.NagerApiClient;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.HolidayNagerResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.CountyRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.DeleteHolidayCandidateRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.HolidayQueryRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.HolidayRepository;
import com.planitsquare.holidaykeeper.domain.holiday.presentation.response.HolidayResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class HolidayService {

    private final HolidayQueryRepository holidayQueryRepository;
    private final NagerApiClient nagerApiClient;
    private final HolidayRepository holidayRepository;
    private final CountyRepository countyRepository;
    private final DeleteHolidayCandidateRepository deleteHolidayCandidateRepository;
    private final CountryService countryService;

    @Transactional
    public void upsertHolidays(int year, String countryCode) {
        upsertHolidays(new HolidayUpsertServiceRequest(year, countryCode));
    }

    private void upsertHolidays(@Valid HolidayUpsertServiceRequest request) {
        Country country = countryService.getCountryOrThrow(request.countryCode());
        List<Holiday> existingHolidays = holidayRepository.findByYearAndCountry(request.year(), country);
        List<HolidayNagerResponse> apiHolidayResponses = nagerApiClient.getPublicHolidays(request.countryCode(), request.year());

        // 1. API 데이터 → 내부 DTO로 변환
        List<HolidaySaveServiceRequest> holidaySaveRequests = apiHolidayResponses.stream()
            .map(apiResponse -> apiResponse.toService(country))
            .toList();

        // 2. Holiday upsert 처리
        for (HolidaySaveServiceRequest saveRequest : holidaySaveRequests) {
            Optional<Holiday> existingHolidayOpt = findExistingHoliday(existingHolidays, saveRequest.date());
            List<County> holidayCounties = convertCounties(saveRequest.countyCodes(), country);

            if (existingHolidayOpt.isPresent()) {
                updateHolidayIfChanged(existingHolidayOpt.get(), saveRequest, holidayCounties);
            } else {
                createAndSaveHoliday(saveRequest, holidayCounties);
            }
        }

        // 3. 삭제 후보 처리
        markDeletedHolidays(existingHolidays, holidaySaveRequests);
    }

    private Optional<Holiday> findExistingHoliday(List<Holiday> existingHolidays, LocalDate date) {
        return existingHolidays.stream().filter(holiday -> holiday.getDate().equals(date)).findFirst();
    }

    private List<County> convertCounties(List<String> CountyCodes, Country country) {
        return CountyCodes.stream().map(countyCode -> findOrCreateCounty(countyCode, country)).toList();
    }

    private void updateHolidayIfChanged(Holiday holiday, HolidaySaveServiceRequest req, List<County> counties) {
        boolean changed = holiday.updateIfChanged(
            req.name(),
            req.localName(),
            req.global(),
            req.types(),
            counties
        );
        if (changed) {
            holidayRepository.save(holiday);
        }
    }

    private void createAndSaveHoliday(HolidaySaveServiceRequest req, List<County> counties) {
        Holiday newHoliday = Holiday.of(
            req.country(),
            req.date(),
            req.localName(),
            req.name(),
            req.fixed(),
            req.global(),
            req.launchYear(),
            req.types(),
            counties
        );
        holidayRepository.save(newHoliday);
    }

    private void markDeletedHolidays(List<Holiday> existing, List<HolidaySaveServiceRequest> saveRequests) {
        Set<LocalDate> apiDates = saveRequests.stream().map(HolidaySaveServiceRequest::date).collect(Collectors.toSet());
        for (Holiday oldData : existing) {
            if (!apiDates.contains(oldData.getDate())) {
                deleteHolidayCandidateRepository.save(DeleteHolidayCandidate.from(oldData));
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<HolidayResponse> search(@Valid HolidaySearchCondition condition, Pageable pageable) {
        return holidayQueryRepository.search(condition, pageable)
                .map(HolidayResponse::from);
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
