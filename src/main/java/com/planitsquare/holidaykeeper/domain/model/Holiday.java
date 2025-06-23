package com.planitsquare.holidaykeeper.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;


@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"country_id", "date"})
)
@NoArgsConstructor
@Getter
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
    private List<HolidayRegion> regions;

    private Integer launchYear;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "holiday_type", joinColumns = @JoinColumn(name = "holiday_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Set<HolidayType> types;

    @Builder
    private Holiday(Country country, LocalDate date, String localName, String name, boolean fixed, boolean global, Integer launchYear, Set<HolidayType> types) {
        this.country = country;
        this.date = date;
        this.localName = localName;
        this.name = name;
        this.fixed = fixed;
        this.regions = new ArrayList<>();
        this.global = global;
        this.launchYear = launchYear;
        this.types = types;
    }

    public void addRegion(Region region) {
        regions.add(new HolidayRegion(this, region));
    }

    public String getCountryCode(){
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
}