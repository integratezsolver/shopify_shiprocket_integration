package com.integratez.platform.modules.shiprocket.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Utility to decode JWT tokens and extract claims
 * Shiprocket returns only a token, so we extract user info from JWT payload
 */
@Component
@Slf4j
public class JwtTokenDecoder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Decode JWT token and extract the payload as JsonNode
     *
     * @param token the JWT token
     * @return JsonNode containing the token claims
     */
    public JsonNode decodeToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        try {
            // JWT format: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.warn("Invalid JWT token format");
                return null;
            }

            // Decode payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readTree(payload);
        } catch (Exception e) {
            log.error("Error decoding JWT token", e);
            return null;
        }
    }

    /**
     * Extract user ID from JWT token
     *
     * @param token the JWT token
     * @return user ID or null
     */
    public Long extractUserId(String token) {
        JsonNode payload = decodeToken(token);
        if (payload == null) {
            return null;
        }

        JsonNode subNode = payload.get("sub");
        if (subNode != null && subNode.isNumber()) {
            return subNode.asLong();
        }

        return null;
    }

    /**
     * Extract customer ID from JWT token
     *
     * @param token the JWT token
     * @return customer ID or null
     */
    public Long extractCustomerId(String token) {
        JsonNode payload = decodeToken(token);
        if (payload == null) {
            return null;
        }

        JsonNode cidNode = payload.get("cid");
        if (cidNode != null && cidNode.isNumber()) {
            return cidNode.asLong();
        }

        return null;
    }

    /**
     * Extract expiration time from JWT token
     *
     * @param token the JWT token
     * @return expiration timestamp or null
     */
    public Long extractExpiration(String token) {
        JsonNode payload = decodeToken(token);
        if (payload == null) {
            return null;
        }

        JsonNode expNode = payload.get("exp");
        if (expNode != null && expNode.isNumber()) {
            return expNode.asLong();
        }

        return null;
    }

    /**
     * Check if token is expired
     *
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        Long expiration = extractExpiration(token);
        if (expiration == null) {
            return true;
        }

        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime > expiration;
    }

    /**
     * Get all claims from token
     *
     * @param token the JWT token
     * @return JsonNode with all claims
     */
    public JsonNode getAllClaims(String token) {
        return decodeToken(token);
    }
}

