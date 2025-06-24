package com.planitsquare.holidaykeeper.domain.repository;

import com.planitsquare.holidaykeeper.domain.model.DeleteCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteCandidateRepository extends JpaRepository<DeleteCandidate, Long> {
}
