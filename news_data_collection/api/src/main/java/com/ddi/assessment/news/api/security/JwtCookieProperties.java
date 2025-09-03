package com.ddi.assessment.news.api.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtCookieProperties {
    private String refreshCookieName;
    private String domain;
    private boolean secure;
    private String sameSite;
    private String path;
}
