package com.planitsquare.holidaykeeper.global.config;

import com.planitsquare.holidaykeeper.domain.country.business.CountryService;
import com.planitsquare.holidaykeeper.domain.country.business.request.CountrySaveServiceRequest;
import com.planitsquare.holidaykeeper.domain.country.business.response.CountryViewServiceResponse;
import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.domain.holiday.business.HolidayService;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryNagerResponse;
import com.planitsquare.holidaykeeper.domain.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.NagerApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidayInitializerRunner implements ApplicationRunner {

    private final CountryRepository countryRepository;
    private final NagerApiClient nagerApiClient;
    private final CountryService countryService;
    private final HolidayService holidayService;

    @Override
    public void run(ApplicationArguments args) {
        List<CountrySaveServiceRequest> CountrySaveServiceRequests = nagerApiClient.getAvailableCountries()
                .stream()
                .map(CountryNagerResponse::toService)
                .toList();


        log.info("국가 정보 초기 데이터 적재 시작");
        countryService.initializeCountries(CountrySaveServiceRequests);
        log.info("국가 정보 초기 데이터 적재 완료");

        List<Country> countries = countryRepository.findAll();
        int currentYear = LocalDate.now().getYear();
        int fromYear = currentYear - 5;
        int toYear = currentYear;


        log.info("공휴일 정보 초기 데이터 적재 시작");
        if (!holidayService.isAlreadyInitialized(fromYear, toYear)) {
            holidayService.initializeHolidays(countries, fromYear, toYear);

        }
        log.info("공휴일 정보 초기 데이터 적재 완료");
    }


}
