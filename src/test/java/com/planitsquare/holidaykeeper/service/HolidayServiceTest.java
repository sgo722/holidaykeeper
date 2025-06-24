package com.planitsquare.holidaykeeper.service;

import com.planitsquare.holidaykeeper.domain.model.*;
import com.planitsquare.holidaykeeper.domain.repository.*;
import com.planitsquare.holidaykeeper.infrastructure.api.NagerApiClient;
import com.planitsquare.holidaykeeper.domain.dto.PublicHolidayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    private DeleteCandidateRepository deleteCandidateRepository;

    @MockBean
    private NagerApiClient nagerApiClient;

    private Country country;

    @BeforeEach
    void setUp() {
        country = countryRepository.save(new Country("KR", "대한민국"));
    }

    @Test
    @DisplayName("신규 공휴일이 정상적으로 DB에 저장된다")
    void insertNewHoliday() {
        // given
        PublicHolidayResponse apiHoliday = new PublicHolidayResponse(
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
        List<Holiday> holidays = holidayRepository.findByYearAndCountry(2025, country);
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
                country,
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

        PublicHolidayResponse apiHoliday = new PublicHolidayResponse(
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
        List<Holiday> holidays = holidayRepository.findByYearAndCountry(2025, country);
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
                country,
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
        List<DeleteCandidate> candidates = deleteCandidateRepository.findAll();
        assertThat(candidates).hasSize(1);
        assertThat(candidates.get(0).getHoliday().getId()).isEqualTo(holiday.getId());
    }

    @Test
    @DisplayName("존재하지 않는 국가코드 입력 시 예외가 발생한다")
    void throwExceptionForInvalidCountryCode() {
        assertThatThrownBy(() -> holidayService.upsertHolidays(2025, "XX"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}