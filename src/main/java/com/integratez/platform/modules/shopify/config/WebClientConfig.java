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
                .defaultHeader("X-Shopify-Access-Token", "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")
                .build();
    }



}
