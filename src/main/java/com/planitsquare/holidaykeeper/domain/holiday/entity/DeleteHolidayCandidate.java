package com.planitsquare.holidaykeeper.domain.holiday.entity;

import com.planitsquare.holidaykeeper.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class DeleteHolidayCandidate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 삭제 후보가 된 Holiday의 정보(복사)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id", nullable = false)
    private Holiday holiday;

    private LocalDate deletedAt;

    public DeleteHolidayCandidate(Holiday holiday) {
        this.holiday = holiday;
        this.deletedAt = LocalDate.now();
    }

    public static DeleteHolidayCandidate from(Holiday holiday) {
        return new DeleteHolidayCandidate(holiday);
    }
}
