package com.ddi.assessment.news.api.scheduler.dto;

import com.ddi.assessment.news.domain.keyword.vo.ExistingKeyword;
import com.ddi.assessment.news.domain.site.vo.ExistingNewsSite;

import java.util.Set;

public record ParsedRequest(
        Set<ExistingKeyword> keywords,
        Set<ExistingNewsSite> sites
) {}
