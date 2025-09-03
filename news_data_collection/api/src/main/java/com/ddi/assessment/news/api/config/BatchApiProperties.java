package com.ddi.assessment.news.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "batch")
public class BatchApiProperties {
    String baseUrl;
    String internalSecret;
}
