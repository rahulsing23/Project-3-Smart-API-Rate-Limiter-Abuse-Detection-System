package com.SmartRatelimiterAndAbuseDetector.config;

import com.SmartRatelimiterAndAbuseDetector.filter.RateLimitingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimiterFilter(
            RateLimitingFilter filter
    ) {
        FilterRegistrationBean<RateLimitingFilter> bean =
                new FilterRegistrationBean<>();

        bean.setFilter(filter);
        bean.addUrlPatterns("/api/*");
        bean.setOrder(1);

        return bean;
    }
}
