package com.planitsquare.holidaykeeper.domain.country.presentation;

import com.planitsquare.holidaykeeper.domain.country.business.CountryService;
import com.planitsquare.holidaykeeper.domain.country.presentation.response.CountryControllerResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/country")
public class CountryController {
    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<List<CountryControllerResponse>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountries().stream()
            .map(CountryControllerResponse::from)
            .toList());
    }
} 