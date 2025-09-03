package com.ddi.assessment.news.domain.keyword.entity;

import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "keywords")
public class JpaKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String word;

    // 연관된 수집 설정 목록
    @OneToMany(mappedBy = "keyword")
    private List<JpaCollectRule> jobConfigs = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public JpaKeyword(String keyword) {
        this.word = keyword;
    }

}
