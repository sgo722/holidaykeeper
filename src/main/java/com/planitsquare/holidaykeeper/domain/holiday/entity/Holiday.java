package com.planitsquare.holidaykeeper.domain.holiday.entity;

import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

import static jakarta.persistence.CascadeType.ALL;


@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"country_id", "date", "localName"})
)
@NoArgsConstructor
@Getter
public class Holiday extends BaseEntity {
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
    private List<HolidayCounty> counties = new ArrayList<>();

    private Integer launchYear;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "holiday_type", joinColumns = @JoinColumn(name = "holiday_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Set<HolidayType> types = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private HolidayStatus holidayStatus;

    @Builder
    private Holiday(Country country, LocalDate date, String localName, String name, boolean fixed, boolean global, Integer launchYear, Set<HolidayType> types) {
        this.country = country;
        this.date = date;
        this.localName = localName;
        this.name = name;
        this.fixed = fixed;
        this.global = global;
        this.launchYear = launchYear;
        this.types = new HashSet<>(types);
        this.holidayStatus = HolidayStatus.ACTIVE;
    }

    public static Holiday of(
            Country country,
            LocalDate date,
            String localName,
            String name,
            boolean fixed,
            boolean global,
            Integer launchYear,
            Set<HolidayType> types,
            List<County> counties
    ) {
        Holiday holiday = new Holiday(country, date, localName, name, fixed, global, launchYear, types);
        for (County county : counties) {
            holiday.addCounty(county);
        }
        return holiday;
    }

    public boolean updateIfChanged(
            String name,
            String localName,
            boolean global,
            Set<HolidayType> types,
            List<County> counties
    ) {
        boolean changed = false;
        if (!Objects.equals(this.name, name)) {
            this.name = name;
            changed = true;
        }
        if (!Objects.equals(this.localName, localName)) {
            this.localName = localName;
            changed = true;
        }
        if (this.global != global) {
            this.global = global;
            changed = true;
        }
        if (!Objects.equals(this.types, types)) {
            this.types = new HashSet<>(types);
            changed = true;
        }
        Set<String> newCountyCodes = counties.stream().map(County::getCode).collect(Collectors.toSet());
        Set<String> oldCountyCodes = this.counties.stream()
            .map(hc -> hc.getCounty().getCode())
            .collect(Collectors.toSet());
        if (!newCountyCodes.equals(oldCountyCodes)) {
            this.counties.clear();
            for (County county : counties) {
                this.addCounty(county);
            }
            changed = true;
        }
        return changed;
    }

    public void addCounty(County county) {
        counties.add(new HolidayCounty(this, county));
    }

    public String getCountryCode() {
        return country.getCode();
    }

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

    public void markAsDeleted() {
        this.holidayStatus = HolidayStatus.DELETE;
    }
}