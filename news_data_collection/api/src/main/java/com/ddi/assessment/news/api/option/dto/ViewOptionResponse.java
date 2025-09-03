package com.ddi.assessment.news.api.option.dto;

import java.util.Set;

public record ViewOptionResponse (Set<IntervalOptionResponse> intervals, Set<NewsSiteOptionResponse> sites) {}
