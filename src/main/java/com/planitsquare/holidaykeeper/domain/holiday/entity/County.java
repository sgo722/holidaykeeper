package com.planitsquare.holidaykeeper.domain.holiday.entity;

import com.planitsquare.holidaykeeper.domain.country.entity.Country;
import com.planitsquare.holidaykeeper.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class County extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Country country;

    protected County() {}

    public County(String code, Country country) {
        this.code = code;
        this.country = country;
    }
}
