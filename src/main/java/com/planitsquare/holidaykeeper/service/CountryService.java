package com.planitsquare.holidaykeeper.service;

import com.planitsquare.holidaykeeper.domain.model.Country;
import com.planitsquare.holidaykeeper.domain.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}