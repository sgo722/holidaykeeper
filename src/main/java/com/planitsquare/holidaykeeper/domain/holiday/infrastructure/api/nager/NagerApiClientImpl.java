package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager;

import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.PublicHolidayResponse;
import com.planitsquare.holidaykeeper.global.exception.NagerApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NagerApiClientImpl implements NagerApiClient {

    private final String GET_COUNTRIES_URI = "/api/v3/AvailableCountries";
    private final String GET_HOLIDAY_URI = "/api/v3/PublicHolidays/{year}/{countryCode}";

    private final RestClient restClient;

    @Override
    public List<CountryResponse> getAvailableCountries() {
        try {
            return restClient.get()
                    .uri(GET_COUNTRIES_URI)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception ex) {
            log.error("Failed to fetch countries from Nager API", ex);
            throw new NagerApiException("나라 목록 조회 실패", ex);
        }
    }

    @Override
    public List<PublicHolidayResponse> getPublicHolidays(String countryCode, int year) {
        try {
            return restClient.get()
                    .uri(GET_HOLIDAY_URI, year, countryCode)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception ex) {
            log.error("Failed to fetch holidays from Nager API: countryCode={}, year={}", countryCode, year, ex);
            throw new NagerApiException("공휴일 조회 실패", ex);
        }
    }
}
