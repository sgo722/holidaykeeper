package com.planitsquare.holidaykeeper.domain.repository;

import com.planitsquare.holidaykeeper.domain.model.Country;
import com.planitsquare.holidaykeeper.domain.model.Holiday;
import com.planitsquare.holidaykeeper.domain.model.HolidayType;
import com.planitsquare.holidaykeeper.domain.model.Region;
import com.planitsquare.holidaykeeper.global.config.QuerydslConfig;
import com.planitsquare.holidaykeeper.service.request.HolidaySearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({HolidayQueryRepository.class, QuerydslConfig.class})
class HolidayQueryRepositoryTest {

    @Autowired
    private HolidayQueryRepository holidayQueryRepository;
    @Autowired
    private HolidayRepository holidayRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private RegionRepository regionRepository;

    @Test
    @DisplayName("날짜와 나라코드로 공휴일을 조회한다")
    void searchHolidaysByYearAndCountryCode() {
        // given
        Country kr = createCountry("KR", "Korea");
        Region seoul = createRegion("SEOUL", kr);
        Holiday holiday = createHoliday("개천절", LocalDate.of(2024, 10, 3), kr, true, HolidayType.PUBLIC);
        holiday.addRegion(seoul);

        HolidaySearchCondition condition = new HolidaySearchCondition("KR", 2024, 2024, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Holiday> result = holidayQueryRepository.search(condition, pageable).getContent();

        // then
        assertThat(result).hasSize(1).extracting(Holiday::getName).containsExactly("개천절");
    }

    @Test
    @DisplayName("글로벌 여부 조건으로 공휴일을 필터링할 수 있다")
    void searchHolidaysByGlobalCondition() {
        // given
        Country kr = createCountry("KR", "Korea");
        Country jp = createCountry("JP", "Japan");
        createHoliday("개천절", LocalDate.of(2024, 10, 3), kr, true, HolidayType.PUBLIC);
        createHoliday("성탄절", LocalDate.of(2024, 12, 25), jp, false, HolidayType.PUBLIC);

        HolidaySearchCondition condition = new HolidaySearchCondition("KR", 2024, 2024, true, null);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Holiday> result = holidayQueryRepository.search(condition, pageable).getContent();

        // then
        assertThat(result).hasSize(1).extracting(Holiday::getName).containsExactly("개천절");
    }

    @Test
    @DisplayName("공휴일 유형으로 필터링할 수 있다")
    void searchHolidaysByType() {
        // given
        Country kr = createCountry("KR", "Korea");
        createHoliday("개천절", LocalDate.of(2024, 10, 3), kr, true, HolidayType.PUBLIC);
        createHoliday("근로자의날", LocalDate.of(2024, 5, 1), kr, true, HolidayType.OBSERVANCE);

        HolidaySearchCondition condition = new HolidaySearchCondition("KR", 2024, 2024, null, HolidayType.PUBLIC);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Holiday> result = holidayQueryRepository.search(condition, pageable).getContent();

        // then
        assertThat(result).hasSize(1).extracting(Holiday::getName).containsExactly("개천절");
    }

    private Holiday createHoliday(String name, LocalDate date, Country country, boolean isGlobal, HolidayType... types) {
        Holiday holiday = Holiday.builder()
                .name(name)
                .date(date)
                .country(country)
                .global(isGlobal)
                .types(Set.of(types))
                .build();
        return holidayRepository.save(holiday);
    }

    private Country createCountry(String code, String name) {
        return countryRepository.save(new Country(code, name));
    }

    private Region createRegion(String code, Country country) {
        return regionRepository.save(new Region(code, country));
    }
}