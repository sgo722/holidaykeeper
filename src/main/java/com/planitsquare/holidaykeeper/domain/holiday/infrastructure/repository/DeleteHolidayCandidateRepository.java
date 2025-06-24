package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.repository;

import com.planitsquare.holidaykeeper.domain.holiday.entity.DeleteHolidayCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteHolidayCandidateRepository extends JpaRepository<DeleteHolidayCandidate, Long> {
}
