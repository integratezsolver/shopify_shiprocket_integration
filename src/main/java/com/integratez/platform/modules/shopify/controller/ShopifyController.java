package com.integratez.platform.modules.shopify.controller;

import com.integratez.platform.modules.common.domain.AccountStatus;
import com.integratez.platform.modules.common.domain.IntegrationCredentials;
import com.integratez.platform.modules.common.repository.IntegrationAccountRepository;
import com.integratez.platform.modules.common.repository.IntegrationCredentialsRepository;
import com.integratez.platform.modules.shopify.config.ShopifyProperties;
import com.integratez.platform.modules.shopify.service.ShopifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/shopify")
@RequiredArgsConstructor
public class ShopifyController {

    private final IntegrationCredentialsRepository integrationCredentialsRepository;
    private final IntegrationAccountRepository integrationAccountRepository;
    private final ShopifyService shopifyService;


    @GetMapping("/install")
    public ResponseEntity<String> install(@RequestParam String shop) {

        Optional<IntegrationCredentials> existingCredential = integrationCredentialsRepository.findByKeyAndValue("shopName", shop);

        if ((existingCredential.isPresent()) && (integrationAccountRepository.getStatusById(existingCredential.get().getIntegrationAccount().getId()) ==   AccountStatus.ACTIVE) ){

            return ResponseEntity.ok( "https://your-onboarding-page-url.com");
        }

        String redirectUrl= shopifyService.generateInstallUrl(shop);

        String html = """
            <html>
              <body>
                <script>
                  // If inside an iframe, redirect parent to top-level
                  if (window.top !== window.self) {
                      window.top.location.href = '/shopify/toplevel?shop=%s';
                  } else {
                      window.location.href = '%s';
                  }
                </script>
              </body>
            </html>
        """.formatted(shop, redirectUrl);
        return ResponseEntity.ok(html);
    }


    @GetMapping("/toplevel")
    public ResponseEntity<String> toplevel(@RequestParam String shop) {
        String redirectUrl = shopifyService.generateInstallUrl(shop);

        String html = """
            <html>
              <body>
                <script>
                  // Force top window to continue OAuth install
                  window.top.location.href = '%s';
                </script>
              </body>
            </html>
        """.formatted(redirectUrl);
        return ResponseEntity.ok(html);
    }


    @GetMapping("/auth/callback")
    public ResponseEntity<String> callback(@RequestParam Map<String, String> params) {
        try {
            shopifyService.handleShopifyCallback(params);
            String successUrl = "https://your-success-url.com";
            return ResponseEntity.status(302).header("Location", successUrl).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
