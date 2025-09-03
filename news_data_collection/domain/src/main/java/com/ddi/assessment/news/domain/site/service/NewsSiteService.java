package com.ddi.assessment.news.domain.site.service;

import com.ddi.assessment.news.domain.site.entity.JpaNewsSite;
import com.ddi.assessment.news.domain.site.vo.NewsSiteQuery;
import com.ddi.assessment.news.domain.site.vo.ExistingNewsSite;

import java.util.Set;

public interface NewsSiteService {
    JpaNewsSite getSite(Long siteId);
    JpaNewsSite getSite(String siteName);
    ExistingNewsSite getSite(NewsSiteQuery query);
    Set<ExistingNewsSite> getSites(Set<NewsSiteQuery> newsSiteQueries);
    Set<ExistingNewsSite> getAllSties();
}
