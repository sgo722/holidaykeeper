package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager;

import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.PublicHolidayResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NagerApiClientImplTest {

    private MockWebServer mockWebServer;
    private NagerApiClientImpl nagerApiClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        nagerApiClient = new NagerApiClientImpl(restClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("나라 외부 API 요청 URL 및 메서드 형식 확인")
    void shouldCountryAPISendCorrectRequest() throws Exception {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setBody("[]")
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // when
        nagerApiClient.getAvailableCountries();

        // then
        var recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/api/v3/AvailableCountries");
    }

    @Test
    @DisplayName("나라 외부 API 반환 데이터 파싱 검증")
    void getAvailableCountries_returnsValidResponse() {
        // given
        String responseBody = """
                [
                  {
                      "countryCode": "AD",
                      "name": "Andorra"
                    },
                    {
                      "countryCode": "AL",
                      "name": "Albania"
                    }
                ]
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // when
        List<CountryResponse> result = nagerApiClient.getAvailableCountries();

        // then
        assertThat(result).hasSize(2);
        CountryResponse countries = result.get(0);
        assertThat(countries.countryCode()).isEqualTo("AD");
        assertThat(countries.name()).isEqualTo("Andorra");
    }


    @Test
    @DisplayName("공휴일 외부 API 요청 URL 및 메서드 형식 확인")
    void shouldHolidayAPISendCorrectRequest() throws Exception {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setBody("[]")
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // when
        nagerApiClient.getPublicHolidays("KR", 2025);

        // then
        var recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/api/v3/PublicHolidays/2025/KR");
    }

    @Test
    @DisplayName("공휴일 외부 API 반환 데이터 파싱 검증")
    void getPublicHolidays_returnsValidResponse() {
        // given
        String responseBody = """
                [
                  {
                    "date": "2025-01-01",
                    "localName": "새해",
                    "name": "New Year's Day",
                    "countryCode": "KR",
                    "fixed": false,
                    "global": true,
                    "county": null,
                    "launchYear": null,
                    "types": ["Public"]
                  }
                ]
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // when
        List<PublicHolidayResponse> result = nagerApiClient.getPublicHolidays("KR", 2025);

        // then
        assertThat(result).hasSize(1);
        PublicHolidayResponse holiday = result.get(0);
        assertThat(holiday.date()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(holiday.name()).isEqualTo("New Year's Day");
        assertThat(holiday.localName()).isEqualTo("새해");
        assertThat(holiday.types()).containsExactly("Public");
    }
}
