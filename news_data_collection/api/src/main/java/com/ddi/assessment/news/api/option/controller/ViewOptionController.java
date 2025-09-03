package com.ddi.assessment.news.api.option.controller;


import com.ddi.assessment.news.api.dto.ResultResponse;
import com.ddi.assessment.news.api.option.application.ViewOptionService;
import com.ddi.assessment.news.api.option.dto.IntervalOptionResponse;
import com.ddi.assessment.news.api.option.dto.NewsSiteOptionResponse;
import com.ddi.assessment.news.api.option.dto.ViewOptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/options")
public class ViewOptionController {

    private final ViewOptionService viewOptionService;

    public ViewOptionController(ViewOptionService viewOptionService) {
        this.viewOptionService = viewOptionService;
    }

    @GetMapping
    public ResponseEntity<ResultResponse<ViewOptionResponse>> getAllOptions() {

        ViewOptionResponse viewOptionResponse = viewOptionService.getOptions();

        return ResponseEntity.ok(ResultResponse.success(viewOptionResponse));

    }

    @GetMapping("/intervals")
    public ResponseEntity<ResultResponse<Set<IntervalOptionResponse>>> getIntervals() {

        Set<IntervalOptionResponse> intervals = viewOptionService.getCronIntervalOptions();

        return ResponseEntity.ok(ResultResponse.success(intervals));

    }

    @GetMapping("/sites")
    public ResponseEntity<ResultResponse<Set<NewsSiteOptionResponse>>> getSites() {

        Set<NewsSiteOptionResponse> sites = viewOptionService.getNewsSiteOptions();

        return ResponseEntity.ok(ResultResponse.success(sites));

    }
}
