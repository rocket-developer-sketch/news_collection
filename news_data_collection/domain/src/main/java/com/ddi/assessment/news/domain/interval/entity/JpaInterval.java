package com.ddi.assessment.news.domain.interval.entity;

import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "intervals")
public class JpaInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;  // 10분, 15분 등

    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;

    @OneToMany(mappedBy = "interval")
    private List<JpaCollectRule> jobConfigs = new ArrayList<>();

    public JpaInterval(String label, String cronExpression) {
        this.label = label;
        this.cronExpression = cronExpression;
    }
}
