package com.ddi.assessment.news.api.scheduler.applicaiton;

import com.ddi.assessment.news.api.scheduler.client.ScheduleApiClient;
import com.ddi.assessment.news.api.scheduler.dto.*;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;
import com.ddi.assessment.news.domain.collectrule.exception.DuplicateCollectRuleException;
import com.ddi.assessment.news.domain.collectrule.service.CollectionRuleService;
import com.ddi.assessment.news.domain.collectrule.vo.ExistingCollectionRule;
import com.ddi.assessment.news.domain.collectrule.vo.NewCollectionRuleCommand;
import com.ddi.assessment.news.domain.collectrule.vo.UpdateCollectionRuleCommand;
import com.ddi.assessment.news.domain.interval.service.IntervalService;
import com.ddi.assessment.news.domain.interval.vo.CronExpQuery;
import com.ddi.assessment.news.domain.interval.vo.IntervalCronExp;
import com.ddi.assessment.news.domain.keyword.service.KeywordService;
import com.ddi.assessment.news.domain.keyword.vo.ExistingKeyword;
import com.ddi.assessment.news.domain.keyword.vo.NewKeywordCommand;

import com.ddi.assessment.news.domain.site.service.NewsSiteService;
import com.ddi.assessment.news.domain.site.vo.NewsSiteQuery;
import com.ddi.assessment.news.domain.site.vo.ExistingNewsSite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ScheduleApiServiceImpl implements ScheduleApiService {
    private final ScheduleApiClient scheduleApiClient;
    private final CollectionRuleService collectionRuleService;
    private final KeywordService keywordService;
    private final IntervalService intervalService;
    private final NewsSiteService newsSiteService;
    private final NewsArticleService newsArticleService;

    public ScheduleApiServiceImpl(ScheduleApiClient scheduleApiClient, CollectionRuleService collectionRuleService, KeywordService keywordService, IntervalService intervalService, NewsSiteService newsSiteService, NewsArticleService newsArticleService) {
        this.scheduleApiClient = scheduleApiClient;
        this.collectionRuleService = collectionRuleService;
        this.keywordService = keywordService;
        this.intervalService = intervalService;
        this.newsSiteService = newsSiteService;
        this.newsArticleService = newsArticleService;
    }

    /**
     * 스케줄 등록 요청
     */
    @Transactional
    public void registerSchedules(Long userId, NewScheduleRequest request) {

        ParsedRequest parsed = parseCommands(request);

        Set<NewCollectionRuleCommand> newCommands = filterNewCombinations(userId, parsed, request.interval());

        if (newCommands.isEmpty()) return;

        List<ExistingCollectionRule> savedRules = savecheduleRules(newCommands);

        registerWithSchedulApi(savedRules, parsed, request.interval());

    }

    /**
     * 스케줄 수정 요청. 주기 & 사용/미사용 수정 가능
     */
    @Transactional
    public void updateSchedule(UpdateScheduleRequest request) {

        IntervalCronExp cronExp = intervalService.getCronExp(new CronExpQuery(request.interval()));

        int updatedCount = collectionRuleService.updateJob(new UpdateCollectionRuleCommand(
                request.ruleId(),
                cronExp.intervalId(),
                cronExp.cronExpression(),
                request.isActive()
        ));

        if (updatedCount > 0) {
            if (request.isActive()) {
                reRegisterWithScheduleApi(request, cronExp);
            } else {
                // 비활성화 스케줄 삭제
                deleteScheduleWithScheduleApi(request.ruleId());
            }
        }

    }

    /**
     * 스케줄 삭제 요청
     */
    @Transactional
    public void deleteSchedule(Long ruleId) {
        // 기사 삭제
        newsArticleService.deleteArticlesByRuleId(ruleId);

        // 사용자 설정 삭제
        collectionRuleService.deleteJob(ruleId);

        deleteScheduleWithScheduleApi(ruleId);
    }

    private ParsedRequest parseCommands(NewScheduleRequest request) {
        Set<NewKeywordCommand> keywords = request.keyword().stream()
                .map(NewKeywordCommand::new)
                .collect(Collectors.toSet());

        Set<NewsSiteQuery> sites = request.newsSite().stream()
                .map(NewsSiteQuery::new)
                .collect(Collectors.toSet());

        Set<ExistingKeyword> existingKeywords = keywordService.findOrCreateKeywords(keywords);
        Set<ExistingNewsSite> existingSites = newsSiteService.getSites(sites);

        return new ParsedRequest(existingKeywords, existingSites);
    }

    private Set<NewCollectionRuleCommand> filterNewCombinations(Long userId, ParsedRequest parsed, String interval) {
        Set<Long> keywordIds = parsed.keywords().stream().map(ExistingKeyword::keywordId).collect(Collectors.toSet());
        Set<Long> siteIds = parsed.sites().stream().map(ExistingNewsSite::siteId).collect(Collectors.toSet());

        Set<ExistingCollectionRule> existing = collectionRuleService
                .getUserRulesByKeywordAndSiteAndInterval(userId, keywordIds, siteIds, interval);

        Set<String> existingKeys = existing.stream()
                .map(rule -> rule.keywordId() + "-" + rule.siteId())
                .collect(Collectors.toSet());

        Set<NewCollectionRuleCommand> result = new HashSet<>();
        for (ExistingKeyword keyword : parsed.keywords()) {
            for (ExistingNewsSite site : parsed.sites()) {
                String key = keyword.keywordId() + "-" + site.siteId();
                if (!existingKeys.contains(key)) {
                    result.add(new NewCollectionRuleCommand(
                            userId,
                            keyword.keywordId(),
                            site.siteId(),
                            keyword.keyword(),
                            site.siteName(),
                            site.urlTemplate(),
                            interval
                    ));
                }
            }
        }
        return result;
    }

    private List<ExistingCollectionRule> savecheduleRules(Set<NewCollectionRuleCommand> commands) {
        return collectionRuleService.createJobs(commands);
    }

    private void registerWithSchedulApi(List<ExistingCollectionRule> rules, ParsedRequest parsed, String interval) {
        String cron = intervalService.getCronExp(new CronExpQuery(interval)).cronExpression();

        Map<Long, ExistingKeyword> keywordMap = parsed.keywords().stream()
                .collect(Collectors.toMap(ExistingKeyword::keywordId, Function.identity()));

        Map<Long, ExistingNewsSite> siteMap = parsed.sites().stream()
                .collect(Collectors.toMap(ExistingNewsSite::siteId, Function.identity()));

        List<NewScheduleApiRequest> requests = rules.stream()
                .map(rule -> {
                    ExistingKeyword k = keywordMap.get(rule.keywordId());
                    ExistingNewsSite s = siteMap.get(rule.siteId());
                    return new NewScheduleApiRequest(
                            rule.ruleId(),
                            k.keywordId(),
                            s.siteId(),
                            k.keyword(),
                            s.siteName(),
                            s.urlTemplate(),
                            cron
                    );
                })
                .toList();

        scheduleApiClient.registerJobs(requests);
    }

    private void reRegisterWithScheduleApi(UpdateScheduleRequest request, IntervalCronExp cronExp) {
        ExistingKeyword keyword = keywordService.findOrCreateKeyword(new NewKeywordCommand(request.keyword()));
        ExistingNewsSite site = newsSiteService.getSite(new NewsSiteQuery(request.siteName()));

        UpdateScheduleApiRequest apiRequest = new UpdateScheduleApiRequest(
                request.ruleId(),
                keyword.keywordId(),
                site.siteId(),
                keyword.keyword(),
                site.siteName(),
                site.urlTemplate(),
                cronExp.cronExpression(),
                true // isActive
        );

        scheduleApiClient.update(apiRequest);
    }

    private void deleteScheduleWithScheduleApi(Long ruleId) {
        scheduleApiClient.delete(ruleId);
    }
}
