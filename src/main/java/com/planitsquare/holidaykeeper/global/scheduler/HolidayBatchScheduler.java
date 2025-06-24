package com.planitsquare.holidaykeeper.global.scheduler;

import com.planitsquare.holidaykeeper.domain.dto.CountryResponse;
import com.planitsquare.holidaykeeper.domain.model.Country;
import com.planitsquare.holidaykeeper.infrastructure.api.NagerApiClient;
import com.planitsquare.holidaykeeper.service.CountryService;
import com.planitsquare.holidaykeeper.service.HolidayService;
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

        log.info("✅ {}년, {}년 모든 외부 API 국가 공휴일 동기화 시작", prevYear, currentYear);

        List<CountryResponse> countries = nagerApiClient.getAvailableCountries();

        for (CountryResponse countryResponse : countries) {
            String code = countryResponse.countryCode();
            String name = countryResponse.name();

            countryService.upsertCountry(code, name);

            log.info("⏳ {}년 {}({}) 공휴일 동기화 시작", currentYear, name, code);
            holidayService.upsertHolidays(currentYear, code);
            log.info("⏳ {}년 {}({}) 공휴일 동기화 시작", prevYear, name, code);
            holidayService.upsertHolidays(prevYear, code);
        }
        log.info("✅ {}년, {}년 모든 외부 API 국가 공휴일 동기화 완료", prevYear, currentYear);
    }
}
