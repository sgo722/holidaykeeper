package com.planitsquare.holidaykeeper.domain.country.business;

import com.planitsquare.holidaykeeper.domain.country.business.response.CountryServiceResponse;
import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.domain.country.repository.CountryRepository;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    @Transactional
    public void upsertCountry(String code, String name) {
        Country country = countryRepository.findByCode(code);
        if (country == null) {
            countryRepository.save(new Country(code, name));
            return;
        }
        if (country.shouldUpdateName(name)) {
            country.updateName(name);
            countryRepository.save(country);
        }
    }

    @Transactional(readOnly = true)
    public Country getCountryOrThrow(String code) {
        Country country = countryRepository.findByCode(code);
        if (country == null) {
            throw new IllegalArgumentException("존재하지 않는 국가코드입니다: " + code);
        }
        return country;
    }

    public List<CountryServiceResponse> getAllCountries() {
        return countryRepository.findAll().stream()
            .map(CountryServiceResponse::from)
            .toList();
    }
}