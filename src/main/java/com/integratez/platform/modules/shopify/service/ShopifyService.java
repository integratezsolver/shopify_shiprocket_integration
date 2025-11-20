package com.integratez.platform.modules.shopify.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.integratez.platform.modules.auth.domain.User;
import com.integratez.platform.modules.auth.repository.UserRepository;
import com.integratez.platform.modules.common.domain.AccountStatus;
import com.integratez.platform.modules.common.domain.IntegrationAccount;
import com.integratez.platform.modules.common.domain.IntegrationCredentials;
import com.integratez.platform.modules.common.domain.Platforms;
import com.integratez.platform.modules.common.repository.IntegrationAccountRepository;
import com.integratez.platform.modules.common.repository.IntegrationCredentialsRepository;
import com.integratez.platform.modules.common.repository.PlatformsRepository;
import com.integratez.platform.modules.shopify.config.ShopifyProperties;
import com.integratez.platform.modules.shopify.util.HmacValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.metrics.export.datadog.DatadogProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;


import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Getter
public class ShopifyService {

    @Value("${shopify.api-key}")
    private String API_KEY;

    @Value("${shopify.api-secret}")
    private String API_SECRET;

    @Value("${shopify.redirect-url}")
    private String REDIRECT_URL;

    @Value("${shopify.scopes}")
    private String SCOPES;


    private final WebClient webClient;
    private final UserRepository userRepo;
    private final PlatformsRepository platformRepo;
    private final IntegrationAccountRepository integrationAccountRepo;
    private final IntegrationCredentialsRepository credentialsRepo;
    private final ShopifyProperties props;

        // -----------------------------------------------
        // Generate Shopify Install URL
        // -----------------------------------------------
    public String generateInstallUrl(String shop) {

            return "https://" + shop +
                    "/admin/oauth/authorize?client_id=" + API_KEY +
                    "&scope=" + SCOPES+
                    "&redirect_uri=" + REDIRECT_URL +
                    "&state=" + UUID.randomUUID();
    }

    public String handleShopifyInstall(String shop) {
        Optional<IntegrationAccount> existingAccount = integrationAccountRepo.findByAccountName(shop);
        if (existingAccount.isPresent() && existingAccount.get().getStatus() == AccountStatus.ACTIVE) {
            return "https://your-onboarding-page-url.com";
        }
        return generateInstallUrl(shop);
    }

    public void handleShopifyCallback(Map<String, String> params) {
        if (!HmacValidator.isValidHmac(params, props.getApiSecret())) {
            throw new IllegalArgumentException("Invalid HMAC");
        }

        String shop = params.get("shop");
        String code = params.get("code");
        String token = getAccessToken(shop, code);

        User user = createUserIfNotExists(shop);
        IntegrationAccount account = createShopifyIntegrationAccount(user, shop);
        saveCredentials(account, "shopName", shop);
        saveCredentials(account, "token", token);
    }



    private String getAccessToken(String shop, String code) {
        return WebClient.create()
                .post()
                .uri("https://" + shop + "/admin/oauth/access_token")
                .bodyValue(Map.of(
                        "client_id", API_KEY,
                        "client_secret", API_SECRET,
                        "code", code
                ))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("access_token").asText())
                .block();
    }

    private User createUserIfNotExists(String shop) {
        String email = shop.replace(".myshopify.com", "") + "@shopify-user.com";
        return userRepo.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(email.split("@")[0]);
                    newUser.setEmail(email);
                    newUser.setPassword("SHOPIFY-OAUTH");
                    newUser.setEnabled(true);
                    return userRepo.save(newUser);
                });
    }

    private IntegrationAccount createShopifyIntegrationAccount(User user, String shop) {
        Platforms platform = platformRepo.findByName("Shopify")
                .orElseThrow(() -> new RuntimeException("Shopify platform not found!"));

        return integrationAccountRepo.findByAccountName(shop)
                .orElseGet(() -> {
                    IntegrationAccount account = new IntegrationAccount();
                    account.setUser(user);
                    account.setChannelId(platform);
                    account.setAccountName(shop);
                    account.setStatus(AccountStatus.ACTIVE);
                    return integrationAccountRepo.save(account);
                });
    }

    private void saveCredentials(IntegrationAccount account, String key, String value) {
        IntegrationCredentials credentials = new IntegrationCredentials();
        credentials.setIntegrationAccount(account);
        credentials.setKey(key);
        credentials.setValue(value);
        credentials.setCreatedAt(Instant.now());
        credentials.setUpdatedAt(Instant.now());
        credentials.setUser(account.getUser());
        credentials.setIntegrationAccount(account);

        credentialsRepo.save(credentials);
    }


}
