package com.integratez.platform.modules.shiprocket.service;

import com.integratez.platform.modules.auth.domain.User;
import com.integratez.platform.modules.auth.repository.UserRepository;
import com.integratez.platform.modules.common.domain.AccountStatus;
import com.integratez.platform.modules.common.domain.IntegrationAccount;
import com.integratez.platform.modules.common.domain.IntegrationCredentials;
import com.integratez.platform.modules.common.domain.Platforms;
import com.integratez.platform.modules.common.repository.IntegrationAccountRepository;
import com.integratez.platform.modules.common.repository.IntegrationCredentialsRepository;
import com.integratez.platform.modules.common.repository.PlatformsRepository;
import com.integratez.platform.modules.shiprocket.config.ShiprocketProperties;
import com.integratez.platform.modules.shiprocket.dto.ShiprocketAuthRequest;
import com.integratez.platform.modules.shiprocket.dto.ShiprocketAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for Shiprocket ERP integration
 * Handles authentication and credential management
 * Stores all responses from Shiprocket API (access token, user info, status codes, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShiprocketService {

    private final WebClient shiprocketWebClient;
    private final ShiprocketProperties shiprocketProperties;
    private final IntegrationAccountRepository integrationAccountRepository;
    private final IntegrationCredentialsRepository integrationCredentialsRepository;
    private final PlatformsRepository platformsRepository;
    private final UserRepository userRepository;

    /**
     * Authenticate with Shiprocket API using email and password
     * Returns the authentication response containing access token and user details
     */
    public Mono<ShiprocketAuthResponse> authenticate(String email, String password) {
        log.info("Attempting Shiprocket authentication for email: {}", email);

        ShiprocketAuthRequest request = ShiprocketAuthRequest.builder()
                .email(email)
                .password(password)
                .build();

        return shiprocketWebClient
                .post()
                .uri(shiprocketProperties.getAuthEndpoint())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ShiprocketAuthResponse.class)
                .doOnNext(response -> log.info("Shiprocket authentication successful"))
                .doOnError(error -> log.error("Shiprocket authentication failed", error));
    }

    /**
     * Save Shiprocket authentication response and create integration account
     * Stores all API response data: access token, user info, status codes, messages, etc.
     * Uses email as identifier to find or create user
     */
    public void handleShiprocketCallback(String shiprocketEmail, ShiprocketAuthResponse authResponse, String accountName) {
        log.info("Handling Shiprocket callback for account: {}", accountName);

        try {
            // Validate response
            if (authResponse == null || authResponse.getToken() == null) {
                throw new IllegalArgumentException("Invalid Shiprocket authentication response");
            }

            // Find or create user with Shiprocket email
            User user = userRepository.findByEmail(shiprocketEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + shiprocketEmail));

            // Get or create Shiprocket platform
            Platforms shiprocketPlatform = platformsRepository
                    .findByName("Shiprocket")
                    .orElseThrow(() -> new IllegalArgumentException("Shiprocket platform not found in database"));

            // Check if integration account already exists
            Optional<IntegrationAccount> existingAccount = integrationAccountRepository
                    .findByAccountName(accountName);

            IntegrationAccount integrationAccount;
            if (existingAccount.isPresent()) {
                integrationAccount = existingAccount.get();
                integrationAccount.setStatus(AccountStatus.ACTIVE);
                integrationAccount.setUpdatedAt(Instant.now());
            } else {
                integrationAccount = new IntegrationAccount();
                integrationAccount.setUser(user);
                integrationAccount.setAccountName(accountName);
                integrationAccount.setChannelId(shiprocketPlatform);
                integrationAccount.setStatus(AccountStatus.ACTIVE);
                integrationAccount.setCreatedAt(Instant.now());
                integrationAccount.setUpdatedAt(Instant.now());
            }

            integrationAccount = integrationAccountRepository.save(integrationAccount);

            // Store all Shiprocket API response fields as credentials
            // This includes access token and any other data from the auth response
            storeShiprocketResponse(user, integrationAccount, authResponse);

            log.info("Shiprocket credentials saved successfully for account: {}", accountName);

        } catch (Exception e) {
            log.error("Error handling Shiprocket callback", e);
            throw new RuntimeException("Failed to save Shiprocket credentials: " + e.getMessage(), e);
        }
    }

    /**
     * Store all fields from Shiprocket auth response as credentials
     */
    private void storeShiprocketResponse(User user, IntegrationAccount integrationAccount, ShiprocketAuthResponse authResponse) {
        // Store access token (most important)
        saveCredential(user, integrationAccount, "accessToken", authResponse.getToken());

        // Store status code
        if (authResponse.getStatusCode() != null) {
            saveCredential(user, integrationAccount, "statusCode", authResponse.getStatusCode().toString());
        }

        // Store message
        if (authResponse.getMessage() != null) {
            saveCredential(user, integrationAccount, "message", authResponse.getMessage());
        }

        // Store success flag
        if (authResponse.getSuccess() != null) {
            saveCredential(user, integrationAccount, "success", authResponse.getSuccess().toString());
        }

        // Store user ID from Shiprocket
        if (authResponse.getUserId() != null) {
            saveCredential(user, integrationAccount, "shiprocketUserId", authResponse.getUserId().toString());
        }

        // Store user email from Shiprocket
        if (authResponse.getUserEmail() != null) {
            saveCredential(user, integrationAccount, "shiprocketUserEmail", authResponse.getUserEmail());
        }
    }

    /**
     * Save or update a single credential
     */
    private void saveCredential(User user, IntegrationAccount integrationAccount, String key, String value) {
        // Check if credential already exists for this account and key
        var existingCredentials = integrationCredentialsRepository
                .findByIntegrationAccount(integrationAccount);

        Optional<IntegrationCredentials> existing = existingCredentials.stream()
                .filter(cred -> cred.getKey().equals(key))
                .findFirst();

        IntegrationCredentials credential;
        if (existing.isPresent()) {
            credential = existing.get();
            credential.setValue(value);
            credential.setUpdatedAt(Instant.now());
        } else {
            credential = new IntegrationCredentials();
            credential.setUser(user);
            credential.setIntegrationAccount(integrationAccount);
            credential.setKey(key);
            credential.setValue(value);
            credential.setCreatedAt(Instant.now());
            credential.setUpdatedAt(Instant.now());
        }

        integrationCredentialsRepository.save(credential);
    }

    /**
     * Retrieve stored access token for a Shiprocket account
     */
    public Optional<String> getAccessToken(String accountName) {
        Optional<IntegrationAccount> account = integrationAccountRepository.findByAccountName(accountName);
        if (account.isEmpty()) {
            return Optional.empty();
        }

        return integrationCredentialsRepository
                .findByIntegrationAccount(account.get())
                .stream()
                .filter(cred -> cred.getKey().equals("accessToken"))
                .map(IntegrationCredentials::getValue)
                .findFirst();
    }

    /**
     * Check if a Shiprocket account is already integrated and active
     */
    public boolean isAccountIntegrated(String accountName) {
        return integrationAccountRepository
                .findByAccountName(accountName)
                .map(account -> account.getStatus() == AccountStatus.ACTIVE)
                .orElse(false);
    }

    /**
     * Get all stored credentials for a Shiprocket account
     */
    public Map<String, String> getAccountCredentials(String accountName) {
        Optional<IntegrationAccount> account = integrationAccountRepository.findByAccountName(accountName);
        if (account.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> credentials = new HashMap<>();
        integrationCredentialsRepository
                .findByIntegrationAccount(account.get())
                .forEach(cred -> credentials.put(cred.getKey(), cred.getValue()));

        return credentials;
    }
}

