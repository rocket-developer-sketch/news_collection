package com.ddi.assessment.news.batch.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {
    private final EntityManager entityManager;

    public QuerydslConfig(@Qualifier("newsEntityManagerFactory") EntityManagerFactory emf) {
        this.entityManager = emf.createEntityManager();
    }

    @Bean
    public JPAQueryFactory newsJpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
