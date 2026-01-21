package com.integratez.platform.modules.shiprocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for storing Shiprocket credentials internally
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiprocketCredentials {

    private String email;
    private String password;
    private String token;
    private Long userId;
    private Long accountId;
    private String companyName;
}

