package com.ddi.assessment.news.batch.config;

import com.ddi.assessment.news.batch.filter.InternalApiCallFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final InternalApiProperties internalApiProperties;

    public FilterConfig(InternalApiProperties internalApiProperties) {
        this.internalApiProperties = internalApiProperties;
    }

    @Bean
    public FilterRegistrationBean<InternalApiCallFilter> internalApiCallFilter() {
        FilterRegistrationBean<InternalApiCallFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new InternalApiCallFilter(internalApiProperties.getHeaderSecret()));
        registrationBean.addUrlPatterns("/schedule/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
