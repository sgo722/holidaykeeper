package com.planitsquare.holidaykeeper.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;


@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"country_id", "date"})
)
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(nullable = false)
    private LocalDate date;

    private String localName;
    private String name;
    private boolean fixed;
    private boolean global;

    @OneToMany(mappedBy = "holiday", cascade = ALL, orphanRemoval = true)
    private List<HolidayRegion> holidayRegions;

    private Integer launchYear;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "holiday_type", joinColumns = @JoinColumn(name = "holiday_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Set<HolidayType> types;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Holiday holiday = (Holiday) o;
        return Objects.equals(id, holiday.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}