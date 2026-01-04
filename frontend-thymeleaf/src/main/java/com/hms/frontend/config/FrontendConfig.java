package com.hms.frontend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(FrontendProperties.class)
public class FrontendConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
