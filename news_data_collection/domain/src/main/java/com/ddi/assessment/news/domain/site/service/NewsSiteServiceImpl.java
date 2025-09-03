package com.ddi.assessment.news.domain.site.service;

import com.ddi.assessment.news.domain.site.entity.JpaNewsSite;
import com.ddi.assessment.news.domain.site.repository.JpaNewsSiteRepository;
import com.ddi.assessment.news.domain.site.vo.NewsSiteQuery;
import com.ddi.assessment.news.domain.site.vo.ExistingNewsSite;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsSiteServiceImpl implements NewsSiteService {
    private final JpaNewsSiteRepository jpaNewsSiteRepository;

    public NewsSiteServiceImpl(JpaNewsSiteRepository jpaNewsSiteRepository) {
        this.jpaNewsSiteRepository = jpaNewsSiteRepository;
    }

    @Override
    public JpaNewsSite getSite(Long siteId) {
        Optional<JpaNewsSite> newsSiteOrEmpty = jpaNewsSiteRepository.findById(siteId);

        if(newsSiteOrEmpty.isEmpty()) {
            throw new IllegalArgumentException("News Site Not Found");
        }

        return newsSiteOrEmpty.get();
    }

    @Override
    public JpaNewsSite getSite(String siteName) {
        Optional<JpaNewsSite> newsSiteOrEmpty = jpaNewsSiteRepository.findBySiteName(siteName);

        if(newsSiteOrEmpty.isEmpty()) {
            throw new IllegalArgumentException("News Site Not Found");
        }

        return newsSiteOrEmpty.get();
    }

    public ExistingNewsSite getSite(NewsSiteQuery query) {
        JpaNewsSite jpaNewsSite = getSite(query.siteName());

        return new ExistingNewsSite(jpaNewsSite.getId(), jpaNewsSite.getSiteName(), jpaNewsSite.getUrlTemplate());
    }

    @Override
    public Set<ExistingNewsSite> getSites(Set<NewsSiteQuery> newsSiteQueries) {
        Set<String> siteNames = newsSiteQueries.stream()
                .map(NewsSiteQuery::siteName)
                .collect(Collectors.toSet());

        List<JpaNewsSite> sites = jpaNewsSiteRepository.findBySiteNameIn(siteNames);

        return sites.stream()
                .map(site -> new ExistingNewsSite(
                        site.getId(),
                        site.getSiteName(),
                        site.getUrlTemplate()
                ))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ExistingNewsSite> getAllSties() {

        return jpaNewsSiteRepository.findAll().stream().map(
                site -> new ExistingNewsSite(
                        site.getId(),
                        site.getSiteName(),
                        site.getUrlTemplate()
        )).collect(Collectors.toSet());

    }
}
