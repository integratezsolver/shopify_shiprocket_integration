package com.integratez.platform.modules.shiprocket.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal wrapper for Shiprocket API response
 * Handles the nested data structure from Shiprocket
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiprocketApiResponse {

    @JsonProperty("token")
    private String token;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("data")
    private UserData data;

    /**
     * Nested user data object from Shiprocket
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserData {
        @JsonProperty("id")
        private Long userId;

        @JsonProperty("email")
        private String userEmail;

        @JsonProperty("company_name")
        private String companyName;

        @JsonProperty("account_id")
        private Long accountId;
    }

    /**
     * Convert to ShiprocketAuthResponse DTO
     */
    public ShiprocketAuthResponse toAuthResponse() {
        ShiprocketAuthResponse.ShiprocketAuthResponseBuilder builder = ShiprocketAuthResponse.builder()
                .token(this.token)
                .statusCode(this.statusCode != null ? this.statusCode : 200)
                .message(this.message != null ? this.message : "Authentication successful")
                .success(true);

        // Extract data from nested object if present
        if (data != null) {
                .statusCode(this.statusCode)
                .message(this.message)
                .success(this.success != null ? this.success : (statusCode != null && statusCode == 200));
            builder.accountId(data.accountId);
        }

        return builder.build();
    }
}

