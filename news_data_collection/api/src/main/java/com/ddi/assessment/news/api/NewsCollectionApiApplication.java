package com.ddi.assessment.news.api;


import com.ddi.assessment.news.api.config.BatchApiProperties;
import com.ddi.assessment.news.api.config.CorsProperties;
import com.ddi.assessment.news.api.security.AuthProperties;
import com.ddi.assessment.news.api.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "com.ddi.assessment")
@EnableConfigurationProperties({AuthProperties.class, JwtProperties.class, CorsProperties.class, BatchApiProperties.class })
public class NewsCollectionApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsCollectionApiApplication.class, args);
    }
}
