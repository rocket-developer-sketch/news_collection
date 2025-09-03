package com.ddi.assessment.news.domain.article.entity;


import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;
import com.ddi.assessment.news.domain.site.entity.JpaNewsSite;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "news_articles")
public class JpaNewsArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "news_id")
    private Long newsId;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private JpaNewsSite newsSite;

    @ManyToOne
    @JoinColumn(name = "job_config_id", nullable = false)
    private JpaCollectRule collectRule;

    private String keyword;

    @Column(name = "news_url")
    private String newsUrl;

    private String title;

    @Lob
    private String content;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @CreationTimestamp
    @Column(name = "crawled_at")
    private LocalDateTime crawledAt;

    public JpaNewsArticle(JpaNewsSite newsSite, JpaCollectRule collectRule, String keyword, String newsUrl, String title, String content, LocalDateTime publishedAt, Long newsId) {
        this.newsSite = newsSite;
        this.collectRule = collectRule;
        this.keyword = keyword;
        this.newsUrl = newsUrl;
        this.title = title;
        this.content = content;
        this.publishedAt = publishedAt;
        this.newsId = newsId;
    }
}
