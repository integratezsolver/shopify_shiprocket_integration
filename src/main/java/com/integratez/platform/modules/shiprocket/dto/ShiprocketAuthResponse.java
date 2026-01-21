package com.integratez.platform.modules.shiprocket.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Response DTO for Shiprocket authentication
 * Maps to Shiprocket API response structure
 *
 * Handles both formats:
 * 1. Nested format with data object
 * 2. Flattened format with properties at root level
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiprocketAuthResponse {

    @JsonProperty("token")
    private String token;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("success")
    private Boolean success;

    // Fields that might be in data object or at root level
    @JsonProperty("id")
    private Long userId;

    @JsonProperty("user_id")
    private Long userIdAlt;

    @JsonProperty("email")
    private String userEmail;

    @JsonProperty("user_email")
    private String userEmailAlt;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("account_id")
    private Long accountId;

    @Builder.Default
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperties(String key, Object value) {
        additionalProperties.put(key, value);
    }

    /**
     * Get the user ID from either userId or userIdAlt
     */
    public Long getUserIdEffective() {
        return userId != null ? userId : userIdAlt;
    }

    /**
     * Get the user email from either userEmail or userEmailAlt
     */
    public String getUserEmailEffective() {
        return userEmail != null ? userEmail : userEmailAlt;
    }
}

