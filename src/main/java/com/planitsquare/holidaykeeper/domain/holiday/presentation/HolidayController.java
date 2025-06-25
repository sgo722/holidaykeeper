package com.planitsquare.holidaykeeper.domain.holiday.presentation;

import com.planitsquare.holidaykeeper.domain.holiday.business.HolidayService;
import com.planitsquare.holidaykeeper.domain.holiday.business.request.HolidayUpsertServiceRequest;
import com.planitsquare.holidaykeeper.domain.holiday.presentation.request.HolidayDeleteRequest;
import com.planitsquare.holidaykeeper.domain.holiday.presentation.request.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.domain.holiday.presentation.request.HolidayUpsertRequest;
import com.planitsquare.holidaykeeper.domain.holiday.presentation.response.HolidayResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Holiday API", description = "공휴일 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/holiday")
public class HolidayController {

    private final HolidayService holidayService;

    @Operation(
        summary = "공휴일 검색",
        description = "조건에 따라 공휴일 목록을 페이징 조회합니다.",
        parameters = {
            @Parameter(name = "country", description = "국가코드(예: KR, US)", example = "KR"),
            @Parameter(name = "fromYear", description = "검색 시작 연도(1900~2100)", example = "2024", schema = @Schema(minimum = "1900", maximum = "2100")),
            @Parameter(name = "toYear", description = "검색 종료 연도(1900~2100)", example = "2024", schema = @Schema(minimum = "1900", maximum = "2100")),
            @Parameter(name = "global", description = "전역 공휴일 여부", example = "true"),
            @Parameter(name = "type", description = "공휴일 타입(PUBLIC, BANK, SCHOOL, AUTHORITIES, OPTIONAL, OBSERVANCE)", example = "PUBLIC")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "공휴일 목록 조회 성공",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = HolidayResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = com.planitsquare.holidaykeeper.global.response.ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                content = @Content(schema = @Schema(implementation = com.planitsquare.holidaykeeper.global.response.ErrorResponse.class)))
        }
    )
    @GetMapping
    public ResponseEntity<Page<HolidayResponse>> searchHolidays(
            @Valid HolidaySearchRequest holidaySearchRequest,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(holidayService.search(holidaySearchRequest.toCondition(), pageable));
    }

    @Operation(
        summary = "공휴일 upsert",
        description = "특정 연도/국가의 공휴일을 외부 API 기준으로 upsert(있으면 update, 없으면 insert)합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "공휴일 upsert 요청 예시: {\"year\":2024,\"countryCode\":\"KR\"}",
            required = true,
            content = @Content(schema = @Schema(implementation = HolidayUpsertRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Upsert 완료", content = @Content(schema = @Schema(example = "Upsert 완료"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = com.planitsquare.holidaykeeper.global.response.ErrorResponse.class)))
        }
    )
    @PostMapping
    public ResponseEntity<String> upsertHolidays(
            @RequestBody @Valid HolidayUpsertRequest holidayUpsertRequest) {
        HolidayUpsertServiceRequest serviceRequest = holidayUpsertRequest.toService();
        holidayService.upsertHolidays(serviceRequest.year(), serviceRequest.countryCode());
        return ResponseEntity.ok("Upsert 완료");
    }

    @Operation(
        summary = "공휴일 상태 DELETE로 변경",
        description = "특정 연도/국가의 공휴일 상태를 DELETE로 변경합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "공휴일 삭제 요청 예시: {\"year\":2024,\"countryCode\":\"KR\"}",
            required = true,
            content = @Content(schema = @Schema(implementation = HolidayDeleteRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "상태값 변경 완료", content = @Content(schema = @Schema(example = "N건 상태값 DELETE로 변경 완료"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = com.planitsquare.holidaykeeper.global.response.ErrorResponse.class)))
        }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<String> markHolidaysAsDeleted(
            @RequestBody HolidayDeleteRequest request
    ) {
        int updated = holidayService.markHolidaysAsDeletedByYearAndCountry(request.year(), request.countryCode());
        return ResponseEntity.ok(updated + "건 상태값 DELETE로 변경 완료");
    }
}
