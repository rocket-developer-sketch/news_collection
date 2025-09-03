package com.ddi.assessment.news.domain.collectrule.entity;


import com.ddi.assessment.news.domain.interval.entity.JpaInterval;
import com.ddi.assessment.news.domain.keyword.entity.JpaKeyword;
import com.ddi.assessment.news.domain.site.entity.JpaNewsSite;
import com.ddi.assessment.news.domain.user.entity.JpaUser;
import jakarta.persistence.*;
import lombok.Getter;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "news_data_configs")
public class JpaCollectRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private JpaUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private JpaKeyword keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private JpaNewsSite newsSite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interval_id", nullable = false)
    private JpaInterval interval;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @UpdateTimestamp
    @Column(name = "last_run_at", nullable = false)
    private LocalDateTime lastRunAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public JpaCollectRule(JpaUser user, JpaKeyword keyword, JpaNewsSite newsSite, JpaInterval interval, Boolean isActive) {
        this.user = user;
        this.keyword = keyword;
        this.newsSite = newsSite;
        this.interval = interval;
        this.isActive = isActive;
    }
}
