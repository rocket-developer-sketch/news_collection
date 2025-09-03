package com.ddi.assessment.news.batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "google-api")
public class GoogleNewsApiProperties {
    String key;
    String cx;
    String headerReferer;
}
