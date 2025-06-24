package com.planitsquare.holidaykeeper.domain.holiday.business.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class HolidayUpsertServiceRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("유효한 요청으로 생성할 수 있다")
    void createValidRequest() {
        // when
        HolidayUpsertServiceRequest request = new HolidayUpsertServiceRequest(2025, "KR");

        // then
        assertThat(request.year()).isEqualTo(2025);
        assertThat(request.countryCode()).isEqualTo("KR");
    }

    @Test
    @DisplayName("연도가 1900년보다 작으면 검증 오류가 발생한다")
    void validationErrorWhenYearIsLessThan1900() {
        // when
        HolidayUpsertServiceRequest request = new HolidayUpsertServiceRequest(1899, "KR");
        Set<ConstraintViolation<HolidayUpsertServiceRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> 
            violation.getPropertyPath().toString().equals("year") &&
            violation.getMessage().contains("1900년 이상")
        );
    }

    @Test
    @DisplayName("연도가 2100년보다 크면 검증 오류가 발생한다")
    void validationErrorWhenYearIsGreaterThan2100() {
        // when
        HolidayUpsertServiceRequest request = new HolidayUpsertServiceRequest(2101, "KR");
        Set<ConstraintViolation<HolidayUpsertServiceRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> 
            violation.getPropertyPath().toString().equals("year") &&
            violation.getMessage().contains("2100년 이하")
        );
    }

    @Test
    @DisplayName("국가코드가 null이면 검증 오류가 발생한다")
    void validationErrorWhenCountryCodeIsNull() {
        // when
        HolidayUpsertServiceRequest request = new HolidayUpsertServiceRequest(2025, null);
        Set<ConstraintViolation<HolidayUpsertServiceRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> 
            violation.getPropertyPath().toString().equals("countryCode") &&
            violation.getMessage().contains("필수입니다")
        );
    }

    @Test
    @DisplayName("경계값 테스트: 1900년으로 요청을 생성할 수 있다")
    void createWithMinimumYear() {
        // when
        HolidayUpsertServiceRequest request = new HolidayUpsertServiceRequest(1900, "KR");
        Set<ConstraintViolation<HolidayUpsertServiceRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("경계값 테스트: 2100년으로 요청을 생성할 수 있다")
    void createWithMaximumYear() {
        // when
        HolidayUpsertServiceRequest request = new HolidayUpsertServiceRequest(2100, "KR");
        Set<ConstraintViolation<HolidayUpsertServiceRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("유효한 국가코드로 요청을 생성할 수 있다")
    void createWithValidCountryCodes() {
        // given
        String[] validCountryCodes = {"KR", "US", "JP", "CN", "GB", "DE", "FR", "IT", "ES", "CA"};

        for (String countryCode : validCountryCodes) {
            // when
            HolidayUpsertServiceRequest request = new HolidayUpsertServiceRequest(2025, countryCode);
            Set<ConstraintViolation<HolidayUpsertServiceRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
            assertThat(request.countryCode()).isEqualTo(countryCode);
        }
    }
} 