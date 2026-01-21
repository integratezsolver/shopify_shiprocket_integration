package com.integratez.platform.modules.shiprocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Shiprocket API integration
 */
@Configuration
@ConfigurationProperties(prefix = "shiprocket.api")
@Data
public class ShiprocketProperties {

    private String baseUrl = "https://apiv2.shiprocket.in";
    private String authEndpoint = "/v1/external/auth/login";
    private int connectTimeout = 5000;
    private int readTimeout = 10000;
}

