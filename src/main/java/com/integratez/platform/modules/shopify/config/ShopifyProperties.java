package com.integratez.platform.modules.shopify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shopify")
@Data
public class ShopifyProperties {
    private String apiKey;
    private String apiSecret;
    private String redirectUrl;
    private String scopes;
    private String platformName;
}
