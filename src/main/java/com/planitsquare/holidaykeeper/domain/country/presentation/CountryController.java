package com.planitsquare.holidaykeeper.domain.country.presentation;

import com.planitsquare.holidaykeeper.domain.country.business.CountryService;
import com.planitsquare.holidaykeeper.domain.country.presentation.response.CountryControllerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Country API", description = "국가 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/country")
public class CountryController {
    private final CountryService countryService;

    @Operation(summary = "국가 목록 조회", description = "모든 국가 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "국가 목록 조회 성공",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountryControllerResponse.class)))
    @GetMapping
    public ResponseEntity<List<CountryControllerResponse>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountries().stream()
            .map(CountryControllerResponse::from)
            .toList());
    }
} 