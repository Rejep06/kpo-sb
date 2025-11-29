package hse.kpo.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(NotificationIntegrationProperty.class)
public class RestClientConfig {

    @Bean
    public RestClient notificationRestClient(NotificationIntegrationProperty properties) {
        return RestClient.builder()
                .baseUrl(properties.url())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
