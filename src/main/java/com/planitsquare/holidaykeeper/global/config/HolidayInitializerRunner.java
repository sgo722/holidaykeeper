package com.planitsquare.holidaykeeper.global.config;

import com.planitsquare.holidaykeeper.domain.dto.CountryResponse;
import com.planitsquare.holidaykeeper.domain.dto.PublicHolidayResponse;
import com.planitsquare.holidaykeeper.domain.model.County;
import com.planitsquare.holidaykeeper.domain.model.Country;
import com.planitsquare.holidaykeeper.domain.model.Holiday;
import com.planitsquare.holidaykeeper.domain.model.HolidayType;
import com.planitsquare.holidaykeeper.domain.repository.CountryRepository;
import com.planitsquare.holidaykeeper.domain.repository.HolidayRepository;
import com.planitsquare.holidaykeeper.domain.repository.CountyRepository;
import com.planitsquare.holidaykeeper.infrastructure.api.NagerApiClient;
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
    private final CountyRepository countyRepository;

    @Override
    public void run(ApplicationArguments args){
        synCountries(); // 나라 목록 먼저 동기화
        syncPublicHolidays();
    }

    private void synCountries(){
        log.info("⏳ 국가 정보 초기 적재 시작");
        List<CountryResponse> countries = nagerApiClient.getAvailableCountries();
        countries.forEach(countryResponse -> {
            if (!countryRepository.existsByCode(countryResponse.countryCode())) {
                countryRepository.save(new Country(
                        countryResponse.countryCode(),
                        countryResponse.name()
                ));
            }
        });

        log.info("✅ 국가 정보 적재 완료");
    }


    private void syncPublicHolidays() {
        List<Country> countries = countryRepository.findAll();

        int currentYear = LocalDate.now().getYear();
        int fromYear = currentYear - 5;
        int toYear = currentYear;

        if (isAlreadyInitialized(fromYear, toYear)) {
            log.info("✅ {}~{}년 공휴일 데이터는 이미 존재합니다. 초기화 생략.", fromYear, toYear);
            return;
        }

        log.info("⏳ {}~{}년 공휴일 초기 적재 시작", fromYear, toYear);


        for (Country country : countries) {
            String countryCode = country.getCode();

            for (int year = fromYear; year <= toYear; year++) {
                List<PublicHolidayResponse> responses = nagerApiClient.getPublicHolidays(countryCode, year);

                for (PublicHolidayResponse response : responses) {
                    if (holidayRepository.existsByCountryAndDate(country, response.date())) {
                        continue; // 이미 있으면 skip
                    }

                    Set<HolidayType> types = new HashSet<>();
                    if (response.types() != null) {
                        for (String type : response.types()) {
                            try {
                                types.add(HolidayType.from(type.toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                // 알 수 없는 타입은 무시하거나 로깅
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

                    if (!response.global() && response.countyCodes() != null) {
                        List<County> counties = response.countyCodes().stream()
                                .map(code -> countyRepository.findByCode(code)
                                        .orElseGet(() -> countyRepository.save(
                                                new County(code, country))))
                                .toList();
                        counties.forEach(holiday::addCounty);
                    }

                    holidayRepository.save(holiday);
                }
            }
        }
        log.info("✅ 초기 적재 완료");
    }
    private boolean isAlreadyInitialized(int fromYear, int toYear) {
        return holidayRepository.existsByDateBetween(
                LocalDate.of(fromYear, 1, 1),
                LocalDate.of(toYear, 12, 31)
        );
    }
}
