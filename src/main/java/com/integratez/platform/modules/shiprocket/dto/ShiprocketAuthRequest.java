package com.integratez.platform.modules.shiprocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Shiprocket authentication
 * Based on Shiprocket API documentation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiprocketAuthRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;
}

