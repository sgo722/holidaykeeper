package com.planitsquare.holidaykeeper.domain.model;

import com.planitsquare.holidaykeeper.global.core.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class DeleteCandidate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 삭제 후보가 된 Holiday의 정보(복사)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id", nullable = false)
    private Holiday holiday;

    private LocalDate deletedAt;

    public DeleteCandidate(Holiday holiday) {
        this.holiday = holiday;
        this.deletedAt = LocalDate.now();
    }

    public static DeleteCandidate from(Holiday holiday) {
        return new DeleteCandidate(holiday);
    }
}
