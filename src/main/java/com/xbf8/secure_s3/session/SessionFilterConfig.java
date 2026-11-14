package com.xbf8.secure_s3.session;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionFilterConfig {
    @Bean
    public FilterRegistrationBean<SessionKeyAuthFilter> sessionKeyFilter(
        SessionKeyAuthFilter filter
    ) {
        FilterRegistrationBean<SessionKeyAuthFilter> register = new FilterRegistrationBean<>();
        register.setFilter(filter);
        register.addUrlPatterns("/files/*");
        register.setOrder(1);
        return register;
    }
}
