package com.integratez.platform.modules.shopify.service;

import com.integratez.platform.modules.shopify.util.ShopifyQueries;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopifyPaginationService {

    private final WebClient webClient;

    public ShopifyPaginationService(@Qualifier("webClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Map<String, Object>> fetchAllOrders(String query, int pageSize) {

        List<Map<String, Object>> allOrders = new ArrayList<>();
        String cursor = null;

        while (true) {

            // Use HashMap -> Allows null values ✔
            Map<String, Object> variables = new HashMap<>();
            variables.put("query", query);
            variables.put("first", pageSize);
            variables.put("after", cursor);  // null allowed on first request

            Map<String, Object> body = new HashMap<>();
            body.put("query", ShopifyQueries.GET_ORDERS_BY_DATE);
            body.put("variables", variables);

            Map<String, Object> response = webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("data")) {
                break; // safety check
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            Map<String, Object> orders = (Map<String, Object>) data.get("orders");

            if (orders == null) break;

            List<Map<String, Object>> nodes =
                    (List<Map<String, Object>>) orders.get("nodes");

            if (nodes != null) {
                allOrders.addAll(nodes);
            }

            Map<String, Object> pageInfo =
                    (Map<String, Object>) orders.get("pageInfo");

            if (pageInfo == null) break;

            boolean hasNextPage = Boolean.TRUE.equals(pageInfo.get("hasNextPage"));

            // Debugging (optional)
            // System.out.println("Fetched count: " + nodes.size());
            // System.out.println("Has next page: " + hasNextPage);

            if (!hasNextPage) break;

            cursor = (String) pageInfo.get("endCursor");
        }

        return allOrders;
    }

}

