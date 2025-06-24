package com.planitsquare.holidaykeeper.domain.model;

import com.planitsquare.holidaykeeper.global.core.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
