package com.ddi.assessment.news.domain.site.vo;

public record ExistingNewsSite(
        Long siteId,
        String siteName,
        String urlTemplate
) {
}
