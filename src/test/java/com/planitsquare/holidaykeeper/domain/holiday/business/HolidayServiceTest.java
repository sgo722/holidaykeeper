package com.planitsquare.holidaykeeper.domain.holiday.business;

import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.domain.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.domain.holiday.entity.*;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.CountyRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.DeleteHolidayCandidateRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.HolidayRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.NagerApiClient;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.HolidayNagerResponse;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidaySearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class HolidayServiceTest {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private DeleteHolidayCandidateRepository deleteHolidayCandidateRepository;

    @MockBean
    private NagerApiClient nagerApiClient;

    private Country kr;

    @BeforeEach
    void setUp() {
        kr = countryRepository.save(new Country("KR", "대한민국"));
    }

    @Test
    @DisplayName("국가코드로 공휴일을 조회하면 해당 국가의 공휴일만 반환된다")
    void searchHolidaysByCountryCode() {
        // given
        Country us = countryRepository.save(new Country("US", "미국"));
        County seoul = countyRepository.save(new County("11", kr));
        County la = countyRepository.save(new County("LA", us));

        Holiday kr2024 = Holiday.of(
                kr,
                LocalDate.of(2024, 3, 1),
                "삼일절",
                "Independence Movement Day",
                true,
                true,
                1946,
                Set.of(HolidayType.PUBLIC),
                List.of(seoul)
        );
        Holiday us2024 = Holiday.of(
                us,
                LocalDate.of(2024, 7, 4),
                "Independence Day",
                "Independence Day",
                true,
                true,
                1776,
                Set.of(HolidayType.PUBLIC),
                List.of(la)
        );
        holidayRepository.saveAll(List.of(kr2024, us2024));

        // when
        var cond = new HolidaySearchCondition("KR", null, null, null, null);
        var result = holidayService.search(cond, Pageable.unpaged());

        // then
        assertThat(result.getContent()).extracting("countryCode").containsOnly("KR");
    }

    @Test
    @DisplayName("연도 범위로 공휴일을 조회하면 해당 연도의 공휴일만 반환된다")
    void searchHolidaysByYearRange() {
        // given
        County seoul = countyRepository.save(new County("11", kr));
        Holiday kr2024 = Holiday.of(
                kr,
                LocalDate.of(2024, 3, 1),
                "삼일절",
                "Independence Movement Day",
                true,
                true,
                1946,
                Set.of(HolidayType.PUBLIC),
                List.of(seoul)
        );
        Holiday kr2025School = Holiday.of(
                kr,
                LocalDate.of(2025, 2, 10),
                "개학일",
                "School Opening Day",
                true,
                false,
                2025,
                Set.of(HolidayType.SCHOOL),
                List.of(seoul)
        );
        holidayRepository.saveAll(List.of(kr2024, kr2025School));

        // when
        var cond = new HolidaySearchCondition("KR", 2025, 2025, null, null);
        var result = holidayService.search(cond, Pageable.unpaged());

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("School Opening Day");
    }

    @Test
    @DisplayName("글로벌 여부로 공휴일을 조회하면 global=true인 공휴일만 반환된다")
    void searchHolidaysByGlobalFlag() {
        // given
        County seoul = countyRepository.save(new County("11", kr));
        Holiday globalHoliday = Holiday.of(
                kr,
                LocalDate.of(2024, 3, 1),
                "삼일절",
                "Independence Movement Day",
                true,
                true,
                1946,
                Set.of(HolidayType.PUBLIC),
                List.of(seoul)
        );
        Holiday localHoliday = Holiday.of(
                kr,
                LocalDate.of(2025, 2, 10),
                "개학일",
                "School Opening Day",
                true,
                false,
                2025,
                Set.of(HolidayType.SCHOOL),
                List.of(seoul)
        );
        holidayRepository.saveAll(List.of(globalHoliday, localHoliday));

        // when
        var cond = new HolidaySearchCondition("KR", null, null, true, null);
        var result = holidayService.search(cond, Pageable.unpaged());

        // then
        assertThat(result.getContent()).allMatch(r -> r.global());
    }

    @Test
    @DisplayName("타입으로 공휴일을 조회하면 해당 타입의 공휴일만 반환된다")
    void searchHolidaysByType() {
        // given
        County seoul = countyRepository.save(new County("11", kr));
        Holiday publicHoliday = Holiday.of(
                kr,
                LocalDate.of(2024, 3, 1),
                "삼일절",
                "Independence Movement Day",
                true,
                true,
                1946,
                Set.of(HolidayType.PUBLIC),
                List.of(seoul)
        );
        Holiday schoolHoliday = Holiday.of(
                kr,
                LocalDate.of(2025, 2, 10),
                "개학일",
                "School Opening Day",
                true,
                false,
                2025,
                Set.of(HolidayType.SCHOOL),
                List.of(seoul)
        );
        holidayRepository.saveAll(List.of(publicHoliday, schoolHoliday));

        // when
        var cond = new HolidaySearchCondition("KR", null, null, null, HolidayType.SCHOOL);
        var result = holidayService.search(cond, Pageable.unpaged());

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).types()).contains(HolidayType.SCHOOL);
    }

    @Test
    @DisplayName("신규 공휴일이 정상적으로 DB에 저장된다")
    void insertNewHoliday() {
        // given
        HolidayNagerResponse apiHoliday = new HolidayNagerResponse(
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                true,
                true,
                List.of(),
                1949,
                List.of("PUBLIC")
        );
        when(nagerApiClient.getPublicHolidays("KR", 2025)).thenReturn(List.of(apiHoliday));

        // when
        holidayService.upsertHolidays(2025, "KR");

        // then
        List<Holiday> holidays = holidayRepository.findByYearAndCountry(2025, kr);
        assertThat(holidays).hasSize(1);
        Holiday holiday = holidays.get(0);
        assertThat(holiday.getName()).isEqualTo("New Year's Day");
        assertThat(holiday.getLocalName()).isEqualTo("신정");
        assertThat(holiday.isGlobal()).isTrue();
        assertThat(holiday.getTypes()).contains(HolidayType.PUBLIC);
    }

    @Test
    @DisplayName("기존 공휴일이 외부 API 데이터로 정상 업데이트된다")
    void updateExistingHoliday() {
        // given
        Holiday holiday = Holiday.of(
                kr,
                LocalDate.of(2025, 1, 1),
                "구신정",
                "Old New Year",
                true,
                true,
                1949,
                Set.of(HolidayType.PUBLIC),
                List.of()
        );
        holidayRepository.save(holiday);

        HolidayNagerResponse apiHoliday = new HolidayNagerResponse(
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                true,
                true,
                List.of(),
                1949,
                List.of("PUBLIC")
        );
        when(nagerApiClient.getPublicHolidays("KR", 2025)).thenReturn(List.of(apiHoliday));

        // when
        holidayService.upsertHolidays(2025, "KR");

        // then
        List<Holiday> holidays = holidayRepository.findByYearAndCountry(2025, kr);
        assertThat(holidays).hasSize(1);
        Holiday updated = holidays.get(0);
        assertThat(updated.getName()).isEqualTo("New Year's Day");
        assertThat(updated.getLocalName()).isEqualTo("신정");
    }

    @Test
    @DisplayName("API에 없는 기존 공휴일은 삭제 후보로 저장된다")
    void markHolidayAsDeleteCandidate() {
        // given
        Holiday holiday = Holiday.of(
                kr,
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                true,
                true,
                1949,
                Set.of(HolidayType.PUBLIC),
                List.of()
        );
        holidayRepository.save(holiday);

        when(nagerApiClient.getPublicHolidays("KR", 2025)).thenReturn(List.of());

        // when
        holidayService.upsertHolidays(2025, "KR");

        // then
        List<DeleteHolidayCandidate> candidates = deleteHolidayCandidateRepository.findAll();
        assertThat(candidates).hasSize(1);
        assertThat(candidates.get(0).getHoliday().getId()).isEqualTo(holiday.getId());
    }

    @Test
    @DisplayName("존재하지 않는 국가코드 입력 시 예외가 발생한다")
    void throwExceptionForInvalidCountryCode() {
        assertThatThrownBy(() -> holidayService.upsertHolidays(2025, "XX"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("특정 연도·국가의 공휴일 상태를 DELETE로 변경한다")
    void markHolidaysAsDeletedByYearAndCountry() {
        // given
        County seoul = countyRepository.save(new County("11", kr));
        Holiday h1 = holidayRepository.save(Holiday.of(
                kr,
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                true,
                true,
                1949,
                Set.of(HolidayType.PUBLIC),
                List.of(seoul)
        ));
        Holiday h2 = holidayRepository.save(Holiday.of(
                kr,
                LocalDate.of(2025, 3, 1),
                "삼일절",
                "Independence Movement Day",
                true,
                true,
                1946,
                Set.of(HolidayType.PUBLIC),
                List.of(seoul)
        ));
        // when
        int updated = holidayService.markHolidaysAsDeletedByYearAndCountry(2025, "KR");
        // then
        assertThat(updated).isEqualTo(2);
        List<Holiday> holidays = holidayRepository.findByYearAndCountry(2025, kr);
        assertThat(holidays).allMatch(h -> h.getHolidayStatus() == HolidayStatus.DELETE);
    }
}