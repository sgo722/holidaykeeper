package com.planitsquare.holidaykeeper.domain.country.business;

import com.planitsquare.holidaykeeper.domain.country.business.response.CountryViewServiceResponse;
import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.domain.country.repository.CountryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @Test
    @DisplayName("신규 국가가 저장된다")
    void saveNewCountry() {
        // given
        when(countryRepository.findByCode("KR")).thenReturn(null);
        when(countryRepository.save(any(Country.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // when
        countryService.upsertCountry("KR", "대한민국");
        // then
        verify(countryRepository).save(argThat(c -> c.getCode().equals("KR") && c.getName().equals("대한민국")));
    }

    @Test
    @DisplayName("기존 국가의 이름이 다르면 업데이트된다")
    void updateCountryNameIfDifferent() {
        // given
        Country old = new Country("KR", "Old Name");
        when(countryRepository.findByCode("KR")).thenReturn(old);
        when(countryRepository.save(any(Country.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // when
        countryService.upsertCountry("KR", "New Name");
        // then
        assertThat(old.getName()).isEqualTo("New Name");
        verify(countryRepository).save(old);
    }

    @Test
    @DisplayName("기존 국가의 이름이 같으면 변경되지 않는다")
    void notUpdateIfNameIsSame() {
        // given
        Country country = new Country("KR", "대한민국");
        when(countryRepository.findByCode("KR")).thenReturn(country);
        // when
        countryService.upsertCountry("KR", "대한민국");
        // then
        verify(countryRepository, never()).save(any());
    }

    @Test
    @DisplayName("서비스에서 국가 전체 목록을 반환한다")
    void getAllCountries() {
        // given
        List<Country> countries = List.of(
                new Country("KR", "대한민국"),
                new Country("US", "미국"),
                new Country("JP", "일본")
        );
        when(countryRepository.findAll()).thenReturn(countries);

        // when
        List<CountryViewServiceResponse> allCountries = countryService.getAllCountries();

        // then
        assertThat(allCountries.size()).isEqualTo(3);
    }
}