package com.korberpharma.order_service.korber_pharma_order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient inventoryClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080/v1/korber-pharma/inventory")
                .build();
    }

}
