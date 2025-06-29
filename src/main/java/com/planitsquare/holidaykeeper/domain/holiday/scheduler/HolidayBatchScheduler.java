package com.planitsquare.holidaykeeper.domain.holiday.scheduler;

import com.planitsquare.holidaykeeper.domain.country.business.CountryService;
import com.planitsquare.holidaykeeper.domain.holiday.business.HolidayService;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.NagerApiClient;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryNagerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidayBatchScheduler {

    private final HolidayService holidayService;
    private final CountryService countryService;
    private final NagerApiClient nagerApiClient;

    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void syncCurrentAndPreviousYearHolidays() {

        int currentYear = Year.now().getValue();
        int prevYear = currentYear - 1;

        log.info("{}년, {}년 모든 외부 API 국가 공휴일 동기화 시작", prevYear, currentYear);

        List<CountryNagerResponse> countries = nagerApiClient.getAvailableCountries();

        for (CountryNagerResponse countryNagerResponse : countries) {
            String code = countryNagerResponse.countryCode();
            String name = countryNagerResponse.name();

            countryService.upsertCountry(code, name);

            log.info("{}년 {}({}) 공휴일 동기화 시작", currentYear, name, code);
            holidayService.upsertHolidays(currentYear, code);
            log.info("{}년 {}({}) 공휴일 동기화 시작", prevYear, name, code);
            holidayService.upsertHolidays(prevYear, code);
        }
        log.info("{}년, {}년 모든 외부 API 국가 공휴일 동기화 완료", prevYear, currentYear);
    }
}
