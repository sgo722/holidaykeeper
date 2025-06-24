package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository;

import com.planitsquare.holidaykeeper.domain.model.*;
import com.planitsquare.holidaykeeper.domain.holiday.entity.Holiday;
import com.planitsquare.holidaykeeper.domain.holiday.entity.HolidayType;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidaySearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
public class HolidayQueryRepository extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public HolidayQueryRepository(JPAQueryFactory queryFactory) {
        super(Holiday.class);
        this.queryFactory = queryFactory;
    }

    public Page<Holiday> search(HolidaySearchCondition condition, Pageable pageable) {
        QHoliday holiday = QHoliday.holiday;
        QHolidayCounty holidayCounty = QHolidayCounty.holidayCounty;
        QCounty county = QCounty.county;

        JPQLQuery<Holiday> query = queryFactory
                .selectFrom(holiday)
                .leftJoin(holiday.counties, holidayCounty).fetchJoin()
                .leftJoin(holidayCounty.county, county).fetchJoin()
                .where(
                        eqCountryCode(condition.countryCode()),
                        betweenYear(condition.fromYear(), condition.toYear()),
                        eqGlobal(condition.global()),
                        eqType(condition.type())
                );

        List<Holiday> results = getQuerydsl().applyPagination(pageable, query).fetch();

        return PageableExecutionUtils.getPage(results, pageable, query::fetchCount);
    }

    private BooleanExpression eqCountryCode(String countryCode) {
        return hasText(countryCode) ? QHoliday.holiday.country.code.eq(countryCode) : null;
    }

    private BooleanExpression betweenYear(Integer from, Integer to) {
        if (from != null && to != null) {
            return QHoliday.holiday.date.between(LocalDate.of(from, 1, 1), LocalDate.of(to, 12, 31));
        } else if (from != null) {
            return QHoliday.holiday.date.goe(LocalDate.of(from, 1, 1));
        } else if (to != null) {
            return QHoliday.holiday.date.loe(LocalDate.of(to, 12, 31));
        }
        return null;
    }

    private BooleanExpression eqGlobal(Boolean global) {
        return global != null ? QHoliday.holiday.global.eq(global) : null;
    }

    private BooleanExpression eqType(HolidayType type) {
        return type != null ? QHoliday.holiday.types.any().eq(type) : null;
    }
}
