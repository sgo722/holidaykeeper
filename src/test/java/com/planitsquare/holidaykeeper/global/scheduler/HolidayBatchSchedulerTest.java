package com.planitsquare.holidaykeeper.global.scheduler;

import com.planitsquare.holidaykeeper.domain.dto.CountryResponse;
import com.planitsquare.holidaykeeper.domain.dto.PublicHolidayResponse;
import com.planitsquare.holidaykeeper.domain.model.Country;
import com.planitsquare.holidaykeeper.domain.model.Holiday;
import com.planitsquare.holidaykeeper.domain.model.HolidayType;
import com.planitsquare.holidaykeeper.domain.repository.CountryRepository;
import com.planitsquare.holidaykeeper.domain.repository.HolidayRepository;
import com.planitsquare.holidaykeeper.infrastructure.api.NagerApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class HolidayBatchSchedulerTest {

    @Autowired
    private HolidayBatchScheduler holidayBatchScheduler;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private HolidayRepository holidayRepository;
    @MockBean
    private NagerApiClient nagerApiClient;

    int currentYear;
    int prevYear;
    CountryResponse kr;
    CountryResponse us;

    @BeforeEach
    void setUp() {
        currentYear = java.time.Year.now().getValue();
        prevYear = currentYear - 1;
        kr = new CountryResponse("KR", "대한민국");
        us = new CountryResponse("US", "미국");
    }

    @Test
    @DisplayName("나라가 외부 API 기준으로 모두 추가된다")
    void countryIsAddedFromExternalApi() {
        // given
        when(nagerApiClient.getAvailableCountries()).thenReturn(List.of(kr, us));
        when(nagerApiClient.getPublicHolidays(anyString(), anyInt())).thenReturn(List.of());
        // when
        holidayBatchScheduler.syncCurrentAndPreviousYearHolidays();
        // then
        assertThat(countryRepository.findByCode("KR")).isNotNull();
        assertThat(countryRepository.findByCode("US")).isNotNull();
    }

    @Test
    @DisplayName("기존에 없던 나라 데이터가 추가된다")
    void newCountryIsAdded() {
        // given: DB에는 KR만 존재
        countryRepository.save(new Country("KR", "대한민국"));
        when(nagerApiClient.getAvailableCountries()).thenReturn(List.of(kr, us));
        when(nagerApiClient.getPublicHolidays(anyString(), anyInt())).thenReturn(List.of());
        // when
        holidayBatchScheduler.syncCurrentAndPreviousYearHolidays();
        // then: US가 새로 추가됨
        assertThat(countryRepository.findByCode("US")).isNotNull();
    }

    @Test
    @DisplayName("공휴일이 외부 API 기준으로 제대로 추가된다")
    void holidayIsAddedFromExternalApi() {
        // given
        when(nagerApiClient.getAvailableCountries()).thenReturn(List.of(kr));
        when(nagerApiClient.getPublicHolidays("KR", currentYear)).thenReturn(List.of(
                new PublicHolidayResponse(
                        LocalDate.of(currentYear, 1, 1), "신정", "New Year's Day", true, true, List.of(), 1949, List.of("PUBLIC")
                )
        ));
        when(nagerApiClient.getPublicHolidays("KR", prevYear)).thenReturn(List.of());
        // when
        holidayBatchScheduler.syncCurrentAndPreviousYearHolidays();
        // then
        List<Holiday> holidays = holidayRepository.findByYearAndCountry(currentYear, countryRepository.findByCode("KR"));
        assertThat(holidays).anyMatch(h -> h.getName().equals("New Year's Day"));
    }

    @Test
    @DisplayName("전년도와 금년도 데이터만 추가된다")
    void onlyCurrentAndPreviousYearHolidaysAreAdded() {
        // given
        when(nagerApiClient.getAvailableCountries()).thenReturn(List.of(kr));
        when(nagerApiClient.getPublicHolidays("KR", currentYear)).thenReturn(List.of(
                new PublicHolidayResponse(
                        LocalDate.of(currentYear, 1, 1), "신정", "New Year's Day", true, true, List.of(), 1949, List.of("PUBLIC")
                )
        ));
        when(nagerApiClient.getPublicHolidays("KR", prevYear)).thenReturn(List.of(
                new PublicHolidayResponse(
                        LocalDate.of(prevYear, 3, 1), "삼일절", "Independence Movement Day", true, true, List.of(), 1946, List.of("PUBLIC")
                )
        ));
        // when
        holidayBatchScheduler.syncCurrentAndPreviousYearHolidays();
        // then: 다른 연도 데이터는 없음
        List<Holiday> holidays = holidayRepository.findByYearAndCountry(currentYear, countryRepository.findByCode("KR"));
        assertThat(holidays).anyMatch(h -> h.getName().equals("New Year's Day"));
        List<Holiday> prevHolidays = holidayRepository.findByYearAndCountry(prevYear, countryRepository.findByCode("KR"));
        assertThat(prevHolidays).anyMatch(h -> h.getName().equals("Independence Movement Day"));
        // 2020년 등 다른 연도는 없음
        List<Holiday> other = holidayRepository.findByYearAndCountry(2020, countryRepository.findByCode("KR"));
        assertThat(other).isEmpty();
    }

    @Test
    @DisplayName("기존 Holiday 데이터가 외부 API 데이터로 동기화(업데이트)된다")
    void holidayIsUpdatedFromExternalApi() {
        // given: DB에 이미 저장된 Holiday가 있고, 외부 API에서 같은 날짜/코드의 Holiday가 다른 이름으로 내려옴
        Country savedCountry = countryRepository.save(new Country("KR", "대한민국"));
        Holiday oldHoliday = holidayRepository.save(Holiday.builder()
                .country(savedCountry)
                .date(LocalDate.of(currentYear, 1, 1))
                .name("구이름")
                .localName("구로컬")
                .types(Set.of(HolidayType.PUBLIC))
                .build());
        when(nagerApiClient.getAvailableCountries()).thenReturn(List.of(kr));
        when(nagerApiClient.getPublicHolidays("KR", currentYear)).thenReturn(List.of(
                new PublicHolidayResponse(
                        LocalDate.of(currentYear, 1, 1), "신정", "새이름", true, true, List.of(), 1949, List.of("PUBLIC")
                )
        ));
        when(nagerApiClient.getPublicHolidays("KR", prevYear)).thenReturn(List.of());
        // when
        holidayBatchScheduler.syncCurrentAndPreviousYearHolidays();
        // then: DB의 Holiday 이름이 "새이름"으로 변경되어야 함
        List<Holiday> holidays = holidayRepository.findByYearAndCountry(currentYear, savedCountry);
        assertThat(holidays).anyMatch(h -> h.getName().equals("새이름") && h.getLocalName().equals("신정"));
    }

    @Test
    @DisplayName("기존 Country 데이터가 외부 API 데이터로 동기화(업데이트)된다")
    void countryIsUpdatedFromExternalApi() {
        // given: DB에 이미 저장된 Country가 있고, 외부 API에서 같은 코드의 Country가 다른 이름으로 내려옴
        countryRepository.save(new Country("KR", "Old Name"));
        CountryResponse krNew = new CountryResponse("KR", "New Name");
        when(nagerApiClient.getAvailableCountries()).thenReturn(List.of(krNew));
        when(nagerApiClient.getPublicHolidays(anyString(), anyInt())).thenReturn(List.of());
        // when
        holidayBatchScheduler.syncCurrentAndPreviousYearHolidays();
        // then: DB의 Country 이름이 "New Name"으로 변경되어야 함
        Country updated = countryRepository.findByCode("KR");
        assertThat(updated.getName()).isEqualTo("New Name");
    }
}