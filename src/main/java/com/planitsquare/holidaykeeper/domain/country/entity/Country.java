package com.planitsquare.holidaykeeper.domain.country.entity;

import com.planitsquare.holidaykeeper.global.entity.BaseEntity;
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

    /**
     * 이름이 다르면 true, 같으면 false 반환
     */
    public boolean shouldUpdateName(String newName) {
        return !this.name.equals(newName);
    }

    /**
     * 이름을 변경
     */
    public void updateName(String newName) {
        this.name = newName;
    }
}