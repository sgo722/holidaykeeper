package com.planitsquare.holidaykeeper.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "holiday_region",
        uniqueConstraints = @UniqueConstraint(columnNames = {"holiday_id", "regionCode"})
)
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
}