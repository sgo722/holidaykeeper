package com.planitsquare.holidaykeeper.domain.holiday.presentation;

import com.planitsquare.holidaykeeper.domain.holiday.presentation.request.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.domain.holiday.presentation.request.HolidayUpsertRequest;
import com.planitsquare.holidaykeeper.domain.holiday.presentation.request.HolidayDeleteRequest;
import com.planitsquare.holidaykeeper.domain.holiday.presentation.response.HolidayResponse;
import com.planitsquare.holidaykeeper.domain.holiday.business.HolidayService;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidayUpsertServiceRequest;
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
    public ResponseEntity<Page<HolidayResponse>> searchHolidays(HolidaySearchRequest holidaySearchRequest, Pageable pageable) {
        return ResponseEntity.ok(holidayService.search(holidaySearchRequest.toCondition(), pageable));
    }

    @PostMapping
    public ResponseEntity<String> upsertHolidays(@RequestBody HolidayUpsertRequest holidayUpsertRequest){
        HolidayUpsertServiceRequest serviceRequest = holidayUpsertRequest.toService();
        holidayService.upsertHolidays(serviceRequest.year(), serviceRequest.countryCode());
        return ResponseEntity.ok("Upsert 완료");
    }

    @PatchMapping("/delete")
    public ResponseEntity<String> markHolidaysAsDeleted(
            @RequestBody HolidayDeleteRequest request
    ) {
        int updated = holidayService.markHolidaysAsDeletedByYearAndCountry(request.year(), request.countryCode());
        return ResponseEntity.ok(updated + "건 상태값 DELETE로 변경 완료");
    }
}
