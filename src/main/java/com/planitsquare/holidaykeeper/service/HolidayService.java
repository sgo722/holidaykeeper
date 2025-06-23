package com.planitsquare.holidaykeeper.service;

import com.planitsquare.holidaykeeper.controller.response.HolidayResponse;
import com.planitsquare.holidaykeeper.domain.repository.HolidayQueryRepository;
import com.planitsquare.holidaykeeper.service.request.HolidaySearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayQueryRepository holidayQueryRepository;

    public Page<HolidayResponse> search(HolidaySearchCondition condition, Pageable pageable) {
        return holidayQueryRepository.search(condition, pageable)
                .map(HolidayResponse::from);
    }


}
