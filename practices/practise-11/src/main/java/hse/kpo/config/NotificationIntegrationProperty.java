package hse.kpo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notifications-integration")
public record NotificationIntegrationProperty(String url, String getAllEndpoint) {}
