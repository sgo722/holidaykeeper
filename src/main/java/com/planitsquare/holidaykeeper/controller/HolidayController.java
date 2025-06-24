package com.planitsquare.holidaykeeper.controller;

import com.planitsquare.holidaykeeper.controller.request.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.controller.request.HolidayUpsertRequest;
import com.planitsquare.holidaykeeper.controller.response.HolidayResponse;
import com.planitsquare.holidaykeeper.service.HolidayService;
import com.planitsquare.holidaykeeper.service.request.HolidayUpsertServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/holiday")
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public Page<HolidayResponse> searchHolidays(HolidaySearchRequest holidaySearchRequest, Pageable pageable) {
        return holidayService.search(holidaySearchRequest.toCondition(), pageable);
    }

    @PostMapping
    public ResponseEntity<String> upsertHolidays(@RequestBody HolidayUpsertRequest holidayUpsertRequest){
        HolidayUpsertServiceRequest serviceRequest = holidayUpsertRequest.toService();
        holidayService.upsertHolidays(serviceRequest.year(), serviceRequest.countryCode());
        return ResponseEntity.ok("Upsert 완료");
    }
}
