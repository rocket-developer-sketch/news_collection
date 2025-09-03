package com.ddi.assessment.news.api.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix="jwt-conf")
public class JwtProperties {
    private String secret;
    private long accessTokenValidity;
    private long refreshTokenValidity;
    private JwtCookieProperties cookie;
}
