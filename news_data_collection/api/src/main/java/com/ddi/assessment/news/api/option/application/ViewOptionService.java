package com.ddi.assessment.news.api.option.application;

import com.ddi.assessment.news.api.option.dto.IntervalOptionResponse;
import com.ddi.assessment.news.api.option.dto.NewsSiteOptionResponse;
import com.ddi.assessment.news.api.option.dto.ViewOptionResponse;

import java.util.Set;

public interface ViewOptionService {
    ViewOptionResponse getOptions();
    Set<IntervalOptionResponse> getCronIntervalOptions();
    Set<NewsSiteOptionResponse> getNewsSiteOptions();
}
