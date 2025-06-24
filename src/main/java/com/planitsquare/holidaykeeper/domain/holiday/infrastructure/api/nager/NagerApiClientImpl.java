package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager;

import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.PublicHolidayResponse;
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
        return restClient.get()
                .uri(GET_COUNTRIES_URI)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<PublicHolidayResponse> getPublicHolidays(String countryCode, int year) {
        return restClient.get()
                .uri(GET_HOLIDAY_URI, year, countryCode)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
