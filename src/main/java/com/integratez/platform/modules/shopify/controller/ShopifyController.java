package com.integratez.platform.modules.shopify.controller;

import com.integratez.platform.modules.shopify.service.ShopifyAuthService;
import com.integratez.platform.modules.shopify.util.HmacValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/shopify")
public class ShopifyController {

    @Autowired
    private ShopifyAuthService authService;

    // 1️⃣ Entry endpoint
    @GetMapping("/install")
    public ResponseEntity<String> install(@RequestParam String shop) {
        String redirectUrl = "https://" + shop + "/admin/oauth/authorize?client_id=" +
                authService.getApiKey() +
                "&scope=" + authService.getScopes() +
                "&redirect_uri=" + authService.getRedirectUri();

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

    // 2️⃣ Top-level redirect page (breaks out of iframe)
    @GetMapping("/toplevel")
    public ResponseEntity<String> toplevel(@RequestParam String shop) {
        String redirectUrl = "https://" + shop + "/admin/oauth/authorize?client_id=" +
                authService.getApiKey() +
                "&scope=" + authService.getScopes() +
                "&redirect_uri=" + authService.getRedirectUri();

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

    // 3️⃣ Callback — completes OAuth
    @GetMapping("/auth/callback")
    public ResponseEntity<String> callback(@RequestParam Map<String, String> params) {
        String shop = params.get("shop");

        if (!HmacValidator.isValidHmac(params, authService.getApiSecret())) {
            return ResponseEntity.badRequest().body("Invalid HMAC");
        }

        try {
            String token = authService.exchangeCodeForToken(shop, params.get("code"));

            String html = """
                <html>
                  <body>
                    <h2>✅ Installed for %s</h2>
                    <p>Access token saved successfully!</p>
                    <script>
                      // Redirect to your embedded app home
                      window.top.location.href = "https://admin.shopify.com/store/%s/apps/shiprocket-integration";
                    </script>
                  </body>
                </html>
            """.formatted(shop, shop.replace(".myshopify.com", ""));
            return ResponseEntity.ok(html);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("<h3>Callback error: " + e.getMessage() + "</h3>");
        }
    }
}


