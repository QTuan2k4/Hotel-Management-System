package com.hms.frontend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "frontend")
public class FrontendProperties {
    private String gatewayBaseUrl = "http://localhost:8081";

    public String getGatewayBaseUrl() { return gatewayBaseUrl; }
    public void setGatewayBaseUrl(String gatewayBaseUrl) { this.gatewayBaseUrl = gatewayBaseUrl; }
}
