package com.ddi.assessment.news.api.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix="auth-conf")
public class AuthProperties {
    private List<String> protectedPaths;
}
