package com.ddi.assessment.news.domain.article.repository;

import com.ddi.assessment.news.domain.article.entity.QJpaNewsArticle;
import com.ddi.assessment.news.domain.article.vo.CollectedNewsArticle;
import com.ddi.assessment.news.domain.collectrule.entity.QJpaCollectRule;
import com.ddi.assessment.news.domain.site.entity.QJpaNewsSite;
import com.ddi.assessment.news.domain.user.entity.QJpaUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NewsArticleCustomRepositoryImpl implements NewsArticleCustomRepository {

    private final JPAQueryFactory queryFactory;

    public NewsArticleCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<CollectedNewsArticle> findNewsArticles(Pageable pageable) {
        QJpaNewsArticle article = QJpaNewsArticle.jpaNewsArticle;
        QJpaNewsSite newsSite = QJpaNewsSite.jpaNewsSite;

        List<CollectedNewsArticle> content = queryFactory
                .select(Projections.constructor(
                        CollectedNewsArticle.class,
                        article.id,
                        article.keyword,
                        newsSite.siteName,
                        article.newsUrl,
                        article.title,
                        article.content,
                        article.publishedAt
                ))
                .from(article)
                .join(article.newsSite, newsSite)
                .orderBy(article.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory.select(article.count())
                        .from(article)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<CollectedNewsArticle> findUserNewsArticles(Long userId, Pageable pageable) {
        QJpaNewsArticle article = QJpaNewsArticle.jpaNewsArticle;
        QJpaNewsSite newsSite = QJpaNewsSite.jpaNewsSite;
        QJpaCollectRule rule = QJpaCollectRule.jpaCollectRule;
        QJpaUser user = QJpaUser.jpaUser;

        List<CollectedNewsArticle> content = queryFactory
                .select(Projections.constructor(
                        CollectedNewsArticle.class,
                        article.id,
                        article.keyword,
                        newsSite.siteName,
                        article.newsUrl,
                        article.title,
                        article.content,
                        article.publishedAt
                ))
                .from(article)
                .join(article.newsSite, newsSite)
                .join(article.collectRule, rule)
                .join(rule.user, user)
                .where(user.id.eq(userId))
                .orderBy(article.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                        .select(article.count())
                        .from(article)
                        .join(article.collectRule, rule)
                        .join(rule.user, user)
                        .where(user.id.eq(userId))
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}
