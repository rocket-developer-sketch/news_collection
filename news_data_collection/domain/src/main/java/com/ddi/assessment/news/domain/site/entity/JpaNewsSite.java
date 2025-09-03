package com.ddi.assessment.news.domain.site.entity;


import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "news_sites")
public class JpaNewsSite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_name", unique = true, nullable = false)
    private String siteName;  // NAVER, DAUM 등

    @Column(name = "url_template", nullable = false)
    private String urlTemplate;

    @OneToMany(mappedBy = "newsSite")
    private List<JpaCollectRule> jobConfigs = new ArrayList<>();

    public JpaNewsSite(String siteName, String urlTemplate) {
        this.siteName = siteName;
        this.urlTemplate = urlTemplate;
    }
}
