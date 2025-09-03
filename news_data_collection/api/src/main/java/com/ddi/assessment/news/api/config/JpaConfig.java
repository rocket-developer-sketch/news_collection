package com.ddi.assessment.news.api.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.ddi.assessment.news.domain")
@EntityScan(basePackages = "com.ddi.assessment.news.domain")
public class JpaConfig {
}
