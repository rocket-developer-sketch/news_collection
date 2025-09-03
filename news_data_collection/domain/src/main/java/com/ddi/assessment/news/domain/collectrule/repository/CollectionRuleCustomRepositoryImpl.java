package com.ddi.assessment.news.domain.collectrule.repository;

import com.ddi.assessment.news.domain.collectrule.entity.QJpaCollectRule;
import com.ddi.assessment.news.domain.collectrule.vo.CollectRuleView;
import com.ddi.assessment.news.domain.collectrule.vo.ExistingCollectionRule;
import com.ddi.assessment.news.domain.interval.entity.QJpaInterval;
import com.ddi.assessment.news.domain.keyword.entity.QJpaKeyword;
import com.ddi.assessment.news.domain.site.entity.QJpaNewsSite;
import com.ddi.assessment.news.domain.user.entity.QJpaUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class CollectionRuleCustomRepositoryImpl implements CollectionRuleCustomRepository {
    private final JPAQueryFactory queryFactory;

    public CollectionRuleCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<CollectRuleView> findAllCollectRules(Pageable pageable) {

        QJpaCollectRule rule = QJpaCollectRule.jpaCollectRule;
        QJpaKeyword keyword = QJpaKeyword.jpaKeyword;
        QJpaNewsSite site = QJpaNewsSite.jpaNewsSite;
        QJpaInterval interval = QJpaInterval.jpaInterval;

        List<CollectRuleView> result = queryFactory
                .select(Projections.constructor(
                        CollectRuleView.class,
                        rule.id,
                        keyword.word,
                        site.siteName,
                        interval.label,
                        rule.createdAt,
                        rule.isActive
                ))
                .from(rule)
                .join(rule.keyword, keyword)
                .join(rule.newsSite, site)
                .join(rule.interval, interval)
                .orderBy(rule.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        long total = Optional.ofNullable(
                queryFactory.select(rule.count()).from(rule).fetchOne()
        ).orElse(0L);

        return new PageImpl<>(result, pageable, total);

    }

    @Override
    public Page<CollectRuleView> findUserCollectRules(Long userId, Pageable pageable) {

        QJpaCollectRule rule = QJpaCollectRule.jpaCollectRule;
        QJpaKeyword keyword = QJpaKeyword.jpaKeyword;
        QJpaNewsSite site = QJpaNewsSite.jpaNewsSite;
        QJpaInterval interval = QJpaInterval.jpaInterval;
        QJpaUser user = QJpaUser.jpaUser;

        List<CollectRuleView> result = queryFactory
                .select(Projections.constructor(
                        CollectRuleView.class,
                        rule.id,
                        keyword.word,
                        site.siteName,
                        interval.label,
                        rule.createdAt,
                        rule.isActive
                ))
                .from(rule)
                .join(rule.keyword, keyword)
                .join(rule.newsSite, site)
                .join(rule.interval, interval)
                .join(rule.user, user)
                .where(user.id.eq(userId))
                .orderBy(rule.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                        .select(rule.count())
                        .from(rule)
                        .join(rule.user, user)
                        .where(user.id.eq(userId))
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(result, pageable, total);

    }

    @Override
    public Set<ExistingCollectionRule> findByUserKeywordIdInAndSiteIdInAndInterval(Long userId, Set<Long> keywordIds, Set<Long> newsSiteIds, String interval) {

        QJpaCollectRule rule = QJpaCollectRule.jpaCollectRule;

        List<ExistingCollectionRule> result = queryFactory
                .select(Projections.constructor(
                        ExistingCollectionRule.class,
                        Expressions.constant(userId),
                        rule.id,
                        rule.keyword.id,
                        rule.newsSite.id,
                        rule.interval.id,
                        rule.isActive
                ))
                .from(rule)
                .where(
                        rule.user.id.eq(userId),
                        rule.keyword.id.in(keywordIds),
                        rule.newsSite.id.in(newsSiteIds),
                        rule.interval.label.eq(interval)
                )
                .fetch();

        return new HashSet<>(result);

    }

    @Override
    public Set<ExistingCollectionRule> findByUserKeywordIdInAndSiteIdInAndCronExpression(Long userId, Set<Long> keywordIds, Set<Long> newsSiteIds, String cronExp) {
        QJpaCollectRule rule = QJpaCollectRule.jpaCollectRule;

        List<ExistingCollectionRule> result = queryFactory
                .select(Projections.constructor(
                        ExistingCollectionRule.class,
                        Expressions.constant(userId),
                        rule.id,
                        rule.keyword.id,
                        rule.newsSite.id,
                        rule.interval.id,
                        rule.isActive
                ))
                .from(rule)
                .where(
                        rule.user.id.eq(userId),
                        rule.keyword.id.in(keywordIds),
                        rule.newsSite.id.in(newsSiteIds),
                        rule.interval.cronExpression.eq(cronExp)
                )
                .fetch();

        return new HashSet<>(result);
    }

}
