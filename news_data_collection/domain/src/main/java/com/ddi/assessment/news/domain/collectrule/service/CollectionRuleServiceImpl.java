package com.ddi.assessment.news.domain.collectrule.service;

import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;
import com.ddi.assessment.news.domain.collectrule.exception.DuplicateCollectRuleException;
import com.ddi.assessment.news.domain.collectrule.repository.CollectionRuleRepository;

import com.ddi.assessment.news.domain.collectrule.vo.CollectRuleView;
import com.ddi.assessment.news.domain.collectrule.vo.ExistingCollectionRule;
import com.ddi.assessment.news.domain.collectrule.vo.NewCollectionRuleCommand;
import com.ddi.assessment.news.domain.collectrule.vo.UpdateCollectionRuleCommand;
import com.ddi.assessment.news.domain.interval.entity.JpaInterval;
import com.ddi.assessment.news.domain.interval.service.IntervalService;
import com.ddi.assessment.news.domain.keyword.entity.JpaKeyword;
import com.ddi.assessment.news.domain.keyword.service.KeywordService;
import com.ddi.assessment.news.domain.site.entity.JpaNewsSite;
import com.ddi.assessment.news.domain.site.service.NewsSiteService;
import com.ddi.assessment.news.domain.user.entity.JpaUser;
import com.ddi.assessment.news.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CollectionRuleServiceImpl implements CollectionRuleService {
    private final CollectionRuleRepository collectionRuleRepository;
    private final KeywordService keywordService;
    private final NewsSiteService siteService;
    private final IntervalService intervalService;
    private final UserService userService;

    public CollectionRuleServiceImpl(CollectionRuleRepository collectionRuleRepository,
                                     KeywordService keywordService, NewsSiteService siteService,
                                     IntervalService intervalService, UserService userService) {
        this.collectionRuleRepository = collectionRuleRepository;
        this.keywordService = keywordService;
        this.siteService = siteService;
        this.intervalService = intervalService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public List<ExistingCollectionRule> createJobs(Set<NewCollectionRuleCommand> newCombinations) {
        if(newCombinations.isEmpty()) {
            return List.of();
        }

        List<JpaCollectRule> collectRules = new ArrayList<>();

        for (NewCollectionRuleCommand command : newCombinations) {
            JpaKeyword keyword = keywordService.getKeyword(command.keywordId());
            JpaNewsSite site = siteService.getSite(command.siteId());
            JpaInterval interval = intervalService.getInterval(command.interval());
            JpaUser user = userService.findUser(command.userId());

            JpaCollectRule rule = new JpaCollectRule(user, keyword, site, interval, true);

            collectRules.add(rule);
        }

        List<JpaCollectRule> saved = collectionRuleRepository.saveAll(collectRules);

        return saved.stream()
                .map(rule -> new ExistingCollectionRule(
                        rule.getUser().getId(),
                        rule.getId(),
                        rule.getKeyword().getId(),
                        rule.getNewsSite().getId(),
                        rule.getInterval().getId(),
                        rule.getIsActive()
                ))
                .toList();
    }

    @Override
    public int updateJob(UpdateCollectionRuleCommand command) {

        if(command.configId() == null || command.configId() <= 0) {
            return 0;
        }

        //Optional<JpaCollectRule> configOrEmpty = collectionRuleRepository.findById(command.configId());
        // todo 호출하는 곳에서 사용자 id 받도록 수정 필요. 현재 join fetch 로 필요 없는 정보까지 들고 오고 있음
        Optional<JpaCollectRule> configOrEmpty = collectionRuleRepository.findByIdWithUserKeywordSite(command.configId());

        if(configOrEmpty.isEmpty()) {
            throw new IllegalArgumentException("News Collection Config Not Found");
        }

        Set<ExistingCollectionRule> existingCollectionRules = getUserRulesByKeywordAndSiteAndCronExp(
                configOrEmpty.get().getUser().getId(),
                Set.of(configOrEmpty.get().getKeyword().getId()),
                Set.of(configOrEmpty.get().getNewsSite().getId()),
                command.cronExpression()
        );

        if(!existingCollectionRules.isEmpty()) {
            throw new DuplicateCollectRuleException("Duplicated News Collection Config");
        }

        return collectionRuleRepository.updateIntervalAndIsActive(command.configId(), command.intervalId(), command.isActive());
    }

    @Override
    public JpaCollectRule getCollectConfig(Long configId) {

        Optional<JpaCollectRule> configOrEmpty = collectionRuleRepository.findById(configId);

        if(configOrEmpty.isEmpty()) {
            throw new IllegalArgumentException("News Collection Config Not Found");
        }

        return configOrEmpty.get();
    }

    @Override
    public Page<CollectRuleView> getCollectConfigs(int pageNo, int size) {
        Pageable pageable = PageRequest.of(pageNo -1, size);

        return collectionRuleRepository.findAllCollectRules(pageable);
    }

    @Override
    public Page<CollectRuleView> getUserCollectConfigs(Long userId, int pageNo, int size) {
        Pageable pageable = PageRequest.of(pageNo -1, size);

        return collectionRuleRepository.findUserCollectRules(userId, pageable);
    }

    @Override
    public Set<ExistingCollectionRule> getUserRulesByKeywordAndSiteAndInterval(Long userId, Set<Long> keywordIds, Set<Long> newsSiteIds, String interval) {
        return collectionRuleRepository.findByUserKeywordIdInAndSiteIdInAndInterval(userId, keywordIds, newsSiteIds, interval);
    }

    private Set<ExistingCollectionRule> getUserRulesByKeywordAndSiteAndCronExp(Long userId, Set<Long> keywordIds, Set<Long> newsSiteIds, String cronExp) {
        return collectionRuleRepository.findByUserKeywordIdInAndSiteIdInAndCronExpression(userId, keywordIds, newsSiteIds, cronExp);
    }

    @Transactional
    @Override
    public void deleteJob(Long ruleId) {
        JpaCollectRule job = collectionRuleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ruleId: " + ruleId));

        collectionRuleRepository.delete(job);
    }

    @Override
    public List<JpaCollectRule> getActiveCollectConfig() {
        return collectionRuleRepository.findByIsActiveTrue();
    }

}
