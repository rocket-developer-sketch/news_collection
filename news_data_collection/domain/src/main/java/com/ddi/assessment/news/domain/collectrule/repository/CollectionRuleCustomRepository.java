package com.ddi.assessment.news.domain.collectrule.repository;

import com.ddi.assessment.news.domain.collectrule.vo.CollectRuleView;
import com.ddi.assessment.news.domain.collectrule.vo.ExistingCollectionRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CollectionRuleCustomRepository {
    Page<CollectRuleView> findAllCollectRules(Pageable pageable);
    Page<CollectRuleView> findUserCollectRules(Long userId, Pageable pageable);
    Set<ExistingCollectionRule> findByUserKeywordIdInAndSiteIdInAndInterval(Long userId, Set<Long> keywordIds, Set<Long> newsSiteIds, String interval);
    Set<ExistingCollectionRule> findByUserKeywordIdInAndSiteIdInAndCronExpression(Long userId, Set<Long> keywordIds, Set<Long> newsSiteIds, String cronExp);
}
