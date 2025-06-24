package com.planitsquare.holidaykeeper.domain.holiday.entity;

import com.planitsquare.holidaykeeper.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "holiday_region",
        uniqueConstraints = @UniqueConstraint(columnNames = {"holiday_id", "counties_id"})
)
@NoArgsConstructor
@Getter
public class HolidayCounty extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id", nullable = false)
    private Holiday holiday;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counties_id", nullable = false)
    private County county;

    public HolidayCounty(Holiday holiday, County county) {
        this.holiday = holiday;
        this.county = county;
    }
}