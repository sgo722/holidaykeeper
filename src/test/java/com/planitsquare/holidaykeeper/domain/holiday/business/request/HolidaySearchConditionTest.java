package com.planitsquare.holidaykeeper.domain.holiday.business.request;

import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;
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

class HolidaySearchConditionTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("유효한 검색 조건으로 생성할 수 있다")
    void createValidSearchCondition() {
        // when
        HolidaySearchCondition condition = new HolidaySearchCondition("KR", 2024, 2025, true, HolidayType.PUBLIC);

        // then
        assertThat(condition.countryCode()).isEqualTo("KR");
        assertThat(condition.fromYear()).isEqualTo(2024);
        assertThat(condition.toYear()).isEqualTo(2025);
        assertThat(condition.global()).isTrue();
        assertThat(condition.type()).isEqualTo(HolidayType.PUBLIC);
    }

    @Test
    @DisplayName("모든 필드가 null인 검색 조건을 생성할 수 있다")
    void createNullSearchCondition() {
        // when
        HolidaySearchCondition condition = new HolidaySearchCondition(null, null, null, null, null);

        // then
        assertThat(condition.countryCode()).isNull();
        assertThat(condition.fromYear()).isNull();
        assertThat(condition.toYear()).isNull();
        assertThat(condition.global()).isNull();
        assertThat(condition.type()).isNull();
    }

    @Test
    @DisplayName("시작 연도가 1900년보다 작으면 검증 오류가 발생한다")
    void validationErrorWhenFromYearIsLessThan1900() {
        // when
        HolidaySearchCondition condition = new HolidaySearchCondition("KR", 1899, 2025, true, HolidayType.PUBLIC);
        Set<ConstraintViolation<HolidaySearchCondition>> violations = validator.validate(condition);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> 
            violation.getPropertyPath().toString().equals("fromYear") &&
            violation.getMessage().contains("1900년 이상")
        );
    }

    @Test
    @DisplayName("종료 연도가 2100년보다 크면 검증 오류가 발생한다")
    void validationErrorWhenToYearIsGreaterThan2100() {
        // when
        HolidaySearchCondition condition = new HolidaySearchCondition("KR", 2024, 2101, true, HolidayType.PUBLIC);
        Set<ConstraintViolation<HolidaySearchCondition>> violations = validator.validate(condition);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> 
            violation.getPropertyPath().toString().equals("toYear") &&
            violation.getMessage().contains("2100년 이하")
        );
    }

    @Test
    @DisplayName("시작 연도가 종료 연도보다 크면 예외가 발생한다")
    void throwExceptionWhenFromYearIsGreaterThanToYear() {
        // when & then
        assertThatThrownBy(() -> new HolidaySearchCondition("KR", 2025, 2024, true, HolidayType.PUBLIC))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 연도는 종료 연도보다 클 수 없습니다");
    }

    @Test
    @DisplayName("경계값 테스트: 1900년으로 요청을 생성할 수 있다")
    void createWithMinimumYear() {
        // when
        HolidaySearchCondition condition = new HolidaySearchCondition("KR", 1900, 1900, true, HolidayType.PUBLIC);
        Set<ConstraintViolation<HolidaySearchCondition>> violations = validator.validate(condition);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("경계값 테스트: 2100년으로 요청을 생성할 수 있다")
    void createWithMaximumYear() {
        // when
        HolidaySearchCondition condition = new HolidaySearchCondition("KR", 2100, 2100, true, HolidayType.PUBLIC);
        Set<ConstraintViolation<HolidaySearchCondition>> violations = validator.validate(condition);

        // then
        assertThat(violations).isEmpty();
    }
}