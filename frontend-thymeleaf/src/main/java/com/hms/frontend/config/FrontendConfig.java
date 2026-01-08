package com.hms.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FrontendConfig implements WebMvcConfigurer {

    private final AdminSecurityInterceptor adminSecurityInterceptor;

    public FrontendConfig(AdminSecurityInterceptor adminSecurityInterceptor) {
        this.adminSecurityInterceptor = adminSecurityInterceptor;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminSecurityInterceptor)
                .addPathPatterns("/admin/**");
    }
}
