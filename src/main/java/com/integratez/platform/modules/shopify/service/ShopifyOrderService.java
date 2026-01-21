package com.integratez.platform.modules.shopify.service;


import com.integratez.platform.modules.shopify.util.ShopifyQueries;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ShopifyOrderService {

    private final WebClient shopifyWebClient;

    public ShopifyOrderService(@Qualifier("webClient") WebClient shopifyWebClient) {
        this.shopifyWebClient = shopifyWebClient;
    }

    public String getOrderById(String orderGid) {
        Map<String, Object> variables = Map.of("id", orderGid);
        return execute(ShopifyQueries.GET_ORDER_BY_ID, variables);
    }

    public String execute(String query, Map<String, Object> variables) {

        Map<String, Object> body = Map.of(
                "query", query,
                "variables", variables
        );

        return shopifyWebClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

