package com.ddi.assessment.news.batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "naver-api")
public class NaverNewsApiProperties {
    List<String> headers;
    String clientId;
    String secret;
}
