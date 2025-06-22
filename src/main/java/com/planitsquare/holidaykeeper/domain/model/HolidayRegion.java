package com.planitsquare.holidaykeeper.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "holiday_region",
        uniqueConstraints = @UniqueConstraint(columnNames = {"holiday_id", "region_id"})
)
@NoArgsConstructor
@Getter
public class HolidayRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id", nullable = false)
    private Holiday holiday;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    public HolidayRegion(Holiday holiday, Region region) {
        this.holiday = holiday;
        this.region = region;
    }
}