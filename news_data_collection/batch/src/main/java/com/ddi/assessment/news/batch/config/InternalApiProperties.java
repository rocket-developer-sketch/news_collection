package com.ddi.assessment.news.batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "internal-api")
public class InternalApiProperties {
    String headerSecret;
}
