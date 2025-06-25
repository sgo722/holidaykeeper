package com.planitsquare.holidaykeeper.global.config;

import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.domain.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.domain.holiday.entity.Holiday;
import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.NagerApiClient;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryNagerResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.HolidayNagerResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.HolidayBulkJdbcRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidayInitializerRunner implements ApplicationRunner {

    private final CountryRepository countryRepository;
    private final NagerApiClient nagerApiClient;
    private final HolidayRepository holidayRepository;
    private final HolidayBulkJdbcRepository holidayBulkJdbcRepository;

    @Override
    public void run(ApplicationArguments args){
        synCountries(); // 나라 목록 먼저 동기화
        syncPublicHolidays();
    }

    private void synCountries(){
        log.info("국가 정보 초기 적재 시작");
        List<CountryNagerResponse> countries = nagerApiClient.getAvailableCountries();
        countries.forEach(countryResponse -> {
            if (!countryRepository.existsByCode(countryResponse.countryCode())) {
                countryRepository.save(new Country(
                        countryResponse.countryCode(),
                        countryResponse.name()
                ));
            }
        });

        log.info("국가 정보 적재 완료");
    }


    private void syncPublicHolidays() {
        try {
            List<Country> countries = countryRepository.findAll();
            int currentYear = LocalDate.now().getYear();
            int fromYear = currentYear - 5;
            int toYear = currentYear;
            if (isAlreadyInitialized(fromYear, toYear)) {
                log.info("{}~{}년 공휴일 데이터는 이미 존재합니다. 초기화 생략.", fromYear, toYear);
                return;
            }
            log.info("{}~{}년 공휴일 초기 적재 시작", fromYear, toYear);

            for (Country country : countries) {
                String countryCode = country.getCode();
                for (int year = fromYear; year <= toYear; year++) {
                    List<HolidayNagerResponse> responses = nagerApiClient.getPublicHolidays(countryCode, year);
                    List<Holiday> holidayCandidates = new java.util.ArrayList<>();
                    for (HolidayNagerResponse response : responses) {
                        Set<HolidayType> types = new HashSet<>();
                        if (response.types() != null) {
                            for (String type : response.types()) {
                                try {
                                    types.add(HolidayType.from(type.toUpperCase()));
                                } catch (IllegalArgumentException e) {
                                    log.info("타입 삽입하다가 에러 발생");
                                }
                            }
                        }
                        Holiday holiday = Holiday.builder()
                                .country(country)
                                .date(response.date())
                                .localName(response.localName())
                                .name(response.name())
                                .fixed(response.fixed())
                                .global(response.global())
                                .types(types)
                                .launchYear(response.launchYear())
                                .build();
                        holidayCandidates.add(holiday);
                    }
                    // DB에서 해당 국가/연도에 이미 존재하는 (date, local_name)만 조회
                    List<Holiday> existing = holidayRepository.findByYearAndCountry(year, country);
                    java.util.Set<String> existingKeySet = existing.stream()
                            .map(h -> h.getDate() + "::" + h.getLocalName())
                            .collect(java.util.stream.Collectors.toSet());
//                     메모리 내에서도 (date, local_name) 기준 중복 제거
                    java.util.Map<String, Holiday> uniqueMap = new java.util.LinkedHashMap<>();
                    for (Holiday h : holidayCandidates) {
                        String key = h.getDate() + "::" + h.getLocalName();
                        if (!existingKeySet.contains(key)) {
                            uniqueMap.putIfAbsent(key, h);
                        }
                    }
                    List<Holiday> toInsert = new java.util.ArrayList<>(uniqueMap.values());
                    if (!toInsert.isEmpty()) {
                        try {
                            holidayBulkJdbcRepository.bulkInsert(toInsert);
                            log.info("{}년 {}({}) 신규 공휴일 {}건 batch insert(JdbcTemplate) 완료", year, country.getName(), countryCode, toInsert.size());
                        } catch (Exception e) {
                            log.error("holiday 테이블 batch insert 실패: {}", e.getMessage());
                        }
                    } else {
                        log.info("{}년 {}({}) 신규 공휴일 없음", year, country.getName(), countryCode);
                    }
                }
            }
            log.info("초기 적재 완료");
        } catch (Exception e) {
            log.error("[HolidayInitializerRunner.syncPublicHolidays] 예외 발생", e);
            throw e;
        }
    }

    private boolean isAlreadyInitialized(int fromYear, int toYear) {
        return holidayRepository.existsByDateBetween(
                LocalDate.of(fromYear, 1, 1),
                LocalDate.of(toYear, 12, 31)
        );
    }
}
