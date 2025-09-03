package com.ddi.assessment.news.domain.collectrule.service;

import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;
import com.ddi.assessment.news.domain.collectrule.vo.CollectRuleView;
import com.ddi.assessment.news.domain.collectrule.vo.ExistingCollectionRule;
import com.ddi.assessment.news.domain.collectrule.vo.NewCollectionRuleCommand;
import com.ddi.assessment.news.domain.collectrule.vo.UpdateCollectionRuleCommand;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface CollectionRuleService {
    List<ExistingCollectionRule> createJobs(Set<NewCollectionRuleCommand> newCombinations);
    int updateJob(UpdateCollectionRuleCommand updateCollectionRuleCommand);
    void deleteJob(Long ruleId);
    List<JpaCollectRule> getActiveCollectConfig();
    JpaCollectRule getCollectConfig(Long configId);
    Page<CollectRuleView> getCollectConfigs(int pageNo, int size);
    Page<CollectRuleView> getUserCollectConfigs(Long userId, int pageNo, int size);
    Set<ExistingCollectionRule> getUserRulesByKeywordAndSiteAndInterval(Long userId, Set<Long> keywordIds, Set<Long> newsSiteIds, String interval);
}
