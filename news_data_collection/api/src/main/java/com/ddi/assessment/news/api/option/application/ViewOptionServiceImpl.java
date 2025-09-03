package com.ddi.assessment.news.api.option.application;

import com.ddi.assessment.news.api.option.dto.IntervalOptionResponse;
import com.ddi.assessment.news.api.option.dto.NewsSiteOptionResponse;
import com.ddi.assessment.news.api.option.dto.ViewOptionResponse;
import com.ddi.assessment.news.domain.interval.service.IntervalService;
import com.ddi.assessment.news.domain.site.service.NewsSiteService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ViewOptionServiceImpl implements ViewOptionService {
    private final NewsSiteService newsSiteService;
    private final IntervalService intervalService;

    public ViewOptionServiceImpl(NewsSiteService newsSiteService, IntervalService intervalService) {
        this.newsSiteService = newsSiteService;
        this.intervalService = intervalService;
    }

    @Override
    public Set<IntervalOptionResponse> getCronIntervalOptions() {
        return intervalService.getAllNewsSites()
                .stream()
                .map(it -> new IntervalOptionResponse(it.interval())
                ).collect(Collectors.toSet());
    }

    @Override
    public Set<NewsSiteOptionResponse> getNewsSiteOptions() {
        return newsSiteService.getAllSties()
                .stream()
                .map(it -> new NewsSiteOptionResponse(it.siteName())
                ).collect(Collectors.toSet());
    }

    @Override
    public ViewOptionResponse getOptions() {
        return new ViewOptionResponse( getCronIntervalOptions(), getNewsSiteOptions());
    }
}
