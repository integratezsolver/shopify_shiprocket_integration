package com.integratez.platform.modules.shiprocket.controller;

import com.integratez.platform.modules.auth.domain.User;
import com.integratez.platform.modules.auth.service.AuthService;
import com.integratez.platform.modules.common.domain.AccountStatus;
import com.integratez.platform.modules.common.domain.IntegrationCredentials;
import com.integratez.platform.modules.common.repository.IntegrationAccountRepository;
import com.integratez.platform.modules.common.repository.IntegrationCredentialsRepository;
import com.integratez.platform.modules.shiprocket.service.ShiprocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Shiprocket ERP integration
 * Similar to ShopifyController, handles Shiprocket auth flows
 * Stores token and account name credentials like Shopify (accountName + token)
 */
@RestController
@RequestMapping("/shiprocket")
@RequiredArgsConstructor
@Slf4j
public class ShiprocketController {

    private final IntegrationCredentialsRepository integrationCredentialsRepository;
    private final IntegrationAccountRepository integrationAccountRepository;
    private final ShiprocketService shiprocketService;
    private final AuthService authService;

    /**
     * Initiate Shiprocket ERP integration
     * Checks if account already exists and is active
     * Otherwise returns credential input form (email + password)
     */
    @GetMapping("/install")
    public ResponseEntity<String> install(@RequestParam String accountName) {
        log.info("Shiprocket install request for account: {}", accountName);

        // Check if account is already integrated and active (same pattern as Shopify)
        Optional<IntegrationCredentials> existingCredential = integrationCredentialsRepository
                .findByKeyAndValue("accountName", accountName);

        if ((existingCredential.isPresent()) &&
            (integrationAccountRepository.getStatusById(existingCredential.get().getIntegrationAccount().getId()) == AccountStatus.ACTIVE)) {
            log.info("Shiprocket account already integrated: {}", accountName);
            return ResponseEntity.ok("https://your-onboarding-page-url.com");
        }

        // Return HTML form for credential input
        String html = """
            <html>
              <head>
                <title>Shiprocket Integration</title>
              </head>
              <body>
                <h1>Connect Shiprocket ERP</h1>
                <p>Please provide your Shiprocket credentials</p>
                <form method="GET" action="/shiprocket/auth/callback">
                  <input type="hidden" name="accountName" value="%s" />
                  <input type="email" name="email" placeholder="Shiprocket Email" required />
                  <input type="password" name="password" placeholder="Shiprocket Password" required />
                  <button type="submit">Connect</button>
                </form>
              </body>
            </html>
        """.formatted(accountName);

        return ResponseEntity.ok(html);
    }

    /**
     * Handles Shiprocket authentication callback
     * Authenticates with Shiprocket API and stores credentials
     */
    @GetMapping("/auth/callback")
    public ResponseEntity<String> callback(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String accountName) {
        log.info("Processing Shiprocket auth callback for account: {}", accountName);

        try {
            // Authenticate with Shiprocket API
            var authResponse = shiprocketService
                    .authenticate(email, password)
                    .block(); // Block for sync response in this endpoint

            if (authResponse == null || authResponse.getToken() == null) {
                log.error("Failed to authenticate with Shiprocket");
                return ResponseEntity.badRequest().body("Shiprocket authentication failed");
            }

            // Save credentials and create integration account (same pattern as Shopify)
            shiprocketService.handleShiprocketCallback(accountName, authResponse);

            log.info("Shiprocket credentials saved for account: {}", accountName);

            // Redirect to success page
            String successUrl = "https://your-onboarding-page-url.com?status=success&account=" + accountName;
            return ResponseEntity.status(302).header("Location", successUrl).build();

        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error handling Shiprocket callback", e);
            return ResponseEntity.status(500).body("Failed to process Shiprocket authentication: " + e.getMessage());
        }
    }

    /**
     * Used by the embedded app (frontend) to exchange Shiprocket account for a JWT
     * Similar to Shopify's app-auth endpoint
     */
    @GetMapping("/app-auth")
    public ResponseEntity<Map<String, String>> appAuth(@RequestParam String accountName) {
        log.info("App-auth request for Shiprocket account: {}", accountName);

        // Find active integration account for this account (same pattern as Shopify)
        var accountOpt = integrationAccountRepository.findByAccountName(accountName);

        if (accountOpt.isEmpty() || accountOpt.get().getStatus() != AccountStatus.ACTIVE) {
            log.warn("Shiprocket account not connected: {}", accountName);
            return ResponseEntity.status(401).body(Map.of("error", "Account not connected"));
        }

        User user = accountOpt.get().getUser();

        // Reuse AuthService to generate JWT with the same claims as normal login
        String token = authService.login(user.getUsername(), "");

        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("username", user.getUsername());
        body.put("email", user.getEmail());
        body.put("accountName", accountName);

        log.info("JWT token generated for Shiprocket app-auth");
        return ResponseEntity.ok(body);
    }

}
