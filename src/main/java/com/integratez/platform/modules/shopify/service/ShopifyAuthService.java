package com.integratez.platform.modules.shopify.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ShopifyAuthService {


    private String apiKey =  "d1a1cebc6be085f5a3a76ee8c5d93ef7";


    public String apiSecret= "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";


    private String redirectUri="https://casimira-inductile-affectedly.ngrok-free.dev/shopify/callback";


    private String scopes="read_orders,write_orders,read_products,write_products,read_fulfillments,write_fulfillments";

    // simple in-memory store (use DB in prod)
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public String getInstallUrl(String shop) {
        return "https://" + shop +
                "/admin/oauth/authorize?client_id=" + apiKey +
                "&scope=" + scopes +
                "&redirect_uri=" + redirectUri;
    }

    public String exchangeCodeForToken(String shop, String code) {
        WebClient client = WebClient.create();
        Map<String, String> response = client.post()
                .uri("https://" + shop + "/admin/oauth/access_token")
                .bodyValue(Map.of(
                        "client_id", apiKey,
                        "client_secret", apiSecret,
                        "code", code))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String token = (String) response.get("access_token");
        tokenStore.put(shop, token);
        return token;
    }

    public String getAccessToken(String shop) {
        return tokenStore.get(shop);
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getScopes() {
        return scopes;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getApiSecret() {
        return apiSecret;
    }
}
