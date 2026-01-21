package com.integratez.platform.modules.shopify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://" + "wffbqx-si" + ".myshopify.com/admin/api/2024-10/graphql.json")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("")
                .build();
    }



}
