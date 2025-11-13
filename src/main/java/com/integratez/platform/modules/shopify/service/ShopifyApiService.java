package com.integratez.platform.modules.shopify.service;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ShopifyApiService {


    private final ShopifyAuthService authService;

    @Autowired
    public ShopifyApiService(ShopifyAuthService authService) {
        this.authService = authService;
    }

    public String getProducts(String shop) {
        String token = authService.getAccessToken(shop);
        if (token == null) return "No token found for shop";

        WebClient client = WebClient.create("https://" + shop);
        return client.post()
                .uri("/admin/api/2025-10/graphql.json")
                .header("Content-Type", "application/json")
                .header("X-Shopify-Access-Token", token)
                .bodyValue("{\"query\": \"{ products(first: 5) { edges { node { id title } } } }\"}")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
