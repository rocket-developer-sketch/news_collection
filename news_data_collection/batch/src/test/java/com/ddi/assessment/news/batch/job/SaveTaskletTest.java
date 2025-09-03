package com.ddi.assessment.news.batch.job;

import com.ddi.assessment.news.batch.config.TestNewsDbConfig;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.domain.article.entity.JpaNewsArticle;
import com.ddi.assessment.news.domain.article.repository.NewsArticleRepository;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;
import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;
import com.ddi.assessment.news.domain.collectrule.repository.CollectionRuleRepository;
import com.ddi.assessment.news.domain.interval.entity.JpaInterval;
import com.ddi.assessment.news.domain.interval.repository.JpaIntervalRepository;
import com.ddi.assessment.news.domain.keyword.entity.JpaKeyword;
import com.ddi.assessment.news.domain.keyword.repository.JpaKeywordRepository;
import com.ddi.assessment.news.domain.site.entity.JpaNewsSite;
import com.ddi.assessment.news.domain.site.repository.JpaNewsSiteRepository;

import com.ddi.assessment.news.domain.user.entity.JpaUser;
import com.ddi.assessment.news.domain.user.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PersistTasklet 뉴스 기사 저장 통합 테스트")
@Transactional("newsTransactionManager")
@ContextConfiguration(classes = {TestNewsDbConfig.class})
@SpringBootTest
@ActiveProfiles("test")
class SaveTaskletTest {

    @Autowired private NewsArticleService articleService;
    @Autowired private NewsArticleRepository articleRepository;
    @Autowired private JpaKeywordRepository keywordRepository;
    @Autowired private JpaNewsSiteRepository siteRepository;
    @Autowired private CollectionRuleRepository configRepository;
    @Autowired private JpaIntervalRepository intervalRepository;
    @Autowired private JpaUserRepository userRepository;

    JpaKeyword keyword1, keyword2;
    JpaNewsSite site1, site2;
    JpaCollectRule config1, config2;
    JpaInterval interval;
    JpaUser user;

    @BeforeEach
    void setUp() {
        interval = intervalRepository.save(new JpaInterval("30분", "0 30 * * * ?"));
        site1 = siteRepository.save(new JpaNewsSite("NAVER", "https://naver.com"));
        site2 = siteRepository.save(new JpaNewsSite("DAUM", "https://daum.net"));
        keyword1 = keywordRepository.save(new JpaKeyword("AI"));
        keyword2 = keywordRepository.save(new JpaKeyword("환율"));
        user = userRepository.save(new JpaUser("testUser1", "testUser1@example.com", "testUserPassword"));
        config1 = configRepository.save(new JpaCollectRule(user, keyword1, site1, interval,true));
        config2 = configRepository.save(new JpaCollectRule(user, keyword2, site2, interval, true));
    }

    @Test
    @DisplayName("최종 수집된 뉴스 데이터 리스트는 뉴스 DB에 정상 저장")
    void shouldPersistParsedArticles_ToNewsDatabase() throws Exception {
        // Given: 테스트용 기사 리스트
        List<ParsedNewsArticle> articles = List.of(
                new ParsedNewsArticle(null, keyword1.getWord(), site1.getSiteName(), "https://example1.test.com/1",
                        "테스트 뉴스 기사 제목1", "테스트 뉴스 기사 본문1", LocalDateTime.now()),
                new ParsedNewsArticle(null, keyword2.getWord(), site2.getSiteName(), "https://example2.test.com/1",
                        "테스트 뉴스 기사 제목2", "테스트 뉴스 기사 본문2", LocalDateTime.now().minusHours(3))
        );

        // And: 테스트용 Job 설정
        NewsCollectionJob testJob = new NewsCollectionJob(
                config2.getId(),
                keyword2.getId(),
                site2.getId(),
                keyword2.getWord(),
                site2.getSiteName(),
                site2.getUrlTemplate(),
                "*/10 * * * *"
        );

        testJob.getParsedArticlesHolder().setArticles(articles);

        SaveTasklet tasklet = new SaveTasklet(testJob, articleService);
        StepExecution stepExecution = new StepExecution("testSaveStep", new JobExecution(1L));
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));
        StepContribution contribution = new StepContribution(stepExecution);

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        List<JpaNewsArticle> saved = articleRepository.findAll();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getTitle()).isEqualTo("테스트 뉴스 기사 제목1");

    }
}
