package com.ddi.assessment.news.api.scheduler.controller;

import com.ddi.assessment.news.api.dto.PageResponse;
import com.ddi.assessment.news.api.dto.ResultResponse;
import com.ddi.assessment.news.api.scheduler.applicaiton.ScheduleApiService;
import com.ddi.assessment.news.api.scheduler.applicaiton.ScheduleService;
import com.ddi.assessment.news.api.scheduler.dto.NewScheduleRequest;

import com.ddi.assessment.news.api.scheduler.dto.UpdateScheduleRequest;
import com.ddi.assessment.news.api.security.JwtUserDetail;
import com.ddi.assessment.news.domain.collectrule.vo.CollectRuleView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/schedule")
public class NewsScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleApiService scheduleApiService;

    public NewsScheduleController(ScheduleService scheduleService, ScheduleApiService scheduleApiService) {
        this.scheduleService = scheduleService;
        this.scheduleApiService = scheduleApiService;
    }

    /**
     * 등록된 스케줄 전체 리스트
     */
    @GetMapping("/all")
    public ResponseEntity<ResultResponse<PageResponse<CollectRuleView>>> getSchedules (
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ResultResponse.success(scheduleService.getSchedules(pageNo, size)));
    }

    @GetMapping
    public ResponseEntity<ResultResponse<PageResponse<CollectRuleView>>> getUserSchedules (
            @AuthenticationPrincipal JwtUserDetail auth,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ResultResponse.success(scheduleService.getUserCollectConfigs(auth.getUserId(), pageNo, size)));
    }

    /**
     * 스케줄 등록
     */
    @PostMapping
    public ResponseEntity<ResultResponse<Void>> registerSchedules(
            @AuthenticationPrincipal JwtUserDetail auth,
            @Valid @RequestBody NewScheduleRequest request) {
        scheduleApiService.registerSchedules(auth.getUserId(), request);

        return ResponseEntity.ok(ResultResponse.success());
    }

    /**
     * 스케줄 수정
     */
    @PutMapping("/{configId}")
    public ResponseEntity<ResultResponse<Void>> updateSchedule(
            @PathVariable Long configId,
            @AuthenticationPrincipal JwtUserDetail auth,
            @Valid @RequestBody UpdateScheduleRequest request) {
        scheduleApiService.updateSchedule(request);

        return ResponseEntity.ok(ResultResponse.success());
    }

    /**
     * 스케줄 삭제
     */
    @DeleteMapping("/{configId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long configId) {
        scheduleApiService.deleteSchedule(configId);
        return ResponseEntity.noContent().build();
    }

}
