package com.planitsquare.holidaykeeper.domain.model;

import com.planitsquare.holidaykeeper.global.core.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Country extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, unique = true, nullable = false)
    private String code;

    @Column(length = 100, nullable = false)
    private String name;

    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }
}