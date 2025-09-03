package com.ddi.assessment.news.batch;

import com.ddi.assessment.news.batch.config.GoogleNewsApiProperties;
import com.ddi.assessment.news.batch.config.InternalApiProperties;
import com.ddi.assessment.news.batch.config.NaverNewsApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({NaverNewsApiProperties.class, GoogleNewsApiProperties.class, InternalApiProperties.class})
@SpringBootApplication(scanBasePackages = "com.ddi.assessment.news")
public class NewsCollectionBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsCollectionBatchApplication.class, args);
    }
}
