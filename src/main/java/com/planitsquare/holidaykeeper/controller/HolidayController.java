package com.planitsquare.holidaykeeper.controller;

import com.planitsquare.holidaykeeper.controller.request.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.controller.response.HolidayResponse;
import com.planitsquare.holidaykeeper.domain.model.HolidayType;
import com.planitsquare.holidaykeeper.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping("/holidays")
    public Page<HolidayResponse> searchHolidays(HolidaySearchRequest holidaySearchRequest, Pageable pageable) {
        return holidayService.search(holidaySearchRequest.toCondition(), pageable);
    }
}
