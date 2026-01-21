package com.integratez.platform.modules.shopify.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.integratez.platform.modules.auth.domain.User;
import com.integratez.platform.modules.auth.repository.UserRepository;
import com.integratez.platform.modules.auth.service.CustomUserDetailsService;
import com.integratez.platform.modules.common.helper.Helper;
import com.integratez.platform.modules.common.repository.IntegrationCredentialsRepository;
import com.integratez.platform.modules.product.domain.ChannelProduct;
import com.integratez.platform.modules.product.domain.ChannelProductInventory;
import com.integratez.platform.modules.product.domain.ChannelProductVariant;
import com.integratez.platform.modules.product.repository.ChannelProductRepository;
import com.integratez.platform.modules.shopify.util.ShopifyQueries;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ShopifyProductService {

    private final WebClient webClient;
    private final ChannelProductRepository channelProductRepository;
    private final UserRepository userRepository;
    private final IntegrationCredentialsRepository credentialsRepository;
    private final CustomUserDetailsService userDetailsService;

    public ShopifyProductService(
            @Qualifier("webClient") WebClient webClient,
            ChannelProductRepository channelProductRepository,
            UserRepository userRepository,
            IntegrationCredentialsRepository credentialsRepository,
            CustomUserDetailsService userDetailsService) {
        this.webClient = webClient;
        this.channelProductRepository = channelProductRepository;
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.userDetailsService = userDetailsService;
    }


    public ChannelProduct fetchAndSaveProduct(String productGid) {

        // Build GraphQL request body
        Map<String, Object> requestBody = Map.of(
                "query", ShopifyQueries.GET_ALL_PRODUCTS,  // your updated inventory query
                "variables", Map.of("id", productGid)
        );

        // Get authenticated user
        String username = userDetailsService.getAuthenticatedUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No authenticated user found!"));

        // Get Shopify shop + access token
        String shop = credentialsRepository.findShopifyShopNameByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Shopify shop not linked for user"));

        String accessToken = credentialsRepository.getShopifyAccessToken(currentUser.getId());

        // Hit Shopify GraphQL
        JsonNode json = webClient.post()
                .uri("https://" + shop + "/admin/api/2024-10/graphql.json")
                .header("X-Shopify-Access-Token", accessToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        JsonNode productNode = json.at("/data/product");

        if (productNode.isMissingNode() || productNode.isNull()) {
            throw new RuntimeException("Product not found on Shopify!");
        }

        // Convert JSON → Entity
        ChannelProduct product = mapToEntity(productNode, currentUser.getId());

        // Save with cascade: product → variants → inventory
        return channelProductRepository.save(product);
    }




    public List<ChannelProduct> fetchAndSaveAllProducts() {

        String username = userDetailsService.getAuthenticatedUsername();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No authenticated user found!"));

        String shop = credentialsRepository.findShopifyShopNameByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Shopify shop not linked for user"));

        String accessToken = credentialsRepository.getShopifyAccessToken(currentUser.getId());

        List<ChannelProduct> savedProducts = new ArrayList<>();

        String cursor = null;
        boolean hasNextPage = true;

        while (hasNextPage) {

            Map<String, Object> variables = new HashMap<>();
            variables.put("first", 50);
            variables.put("after", cursor);

            Map<String, Object> requestBody = Map.of(
                    "query", ShopifyQueries.GET_ALL_PRODUCTS,
                    "variables", variables
            );

            JsonNode response = webClient.post()
                    .uri("https://" + shop + "/admin/api/2024-10/graphql.json")
                    .header("X-Shopify-Access-Token", accessToken)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            JsonNode productsNode = response.at("/data/products");

            for (JsonNode edge : productsNode.at("/edges")) {

                JsonNode productNode = edge.get("node");

                ChannelProduct product = mapToEntity(productNode, currentUser.getId());
                ChannelProduct saved = channelProductRepository.save(product);

                savedProducts.add(saved);
            }

            hasNextPage = productsNode.at("/pageInfo/hasNextPage").asBoolean();
            cursor = productsNode.at("/pageInfo/endCursor").asText(null);
        }

        return savedProducts;
    }





    private ChannelProduct mapToEntity(JsonNode node, Long userId) {

        ChannelProduct product = new ChannelProduct();

        // ---- PRODUCT FIELDS ----
        product.setProductId(extractId(node.get("id")));
        product.setTitle(node.get("title").asText());
        product.setProductType(node.get("productType").asText(""));
        product.setStatus(node.get("status").asText(""));
        product.setTags(node.get("tags").toString());  // tags array saved as string

        product.setChannelId(1L);
        product.setUserId(userId);

        List<ChannelProductVariant> variants = new ArrayList<>();

        // ---- VARIANTS ----
        for (JsonNode edge : node.at("/variants/edges")) {

            JsonNode v = edge.get("node");

            ChannelProductVariant variant = new ChannelProductVariant();

            variant.setVariantId(extractId(v.get("id")));
            variant.setTitle(v.get("title").asText());
            variant.setSku(v.get("sku").asText(""));
            variant.setPrice(v.get("price").asDouble());
            variant.setChannelId(1L);
            variant.setProduct(product); // JPA relation
            variant.setProductId(product.getProductId());

            // Shopify response does NOT contain inventoryItem → set null
            variant.setInventoryItemId(null);

            // Shopify sends 'inventoryQuantity' → you may save it later if needed
            // Example: variant.setInventoryQuantity(v.get("inventoryQuantity").asInt());

            // No inventory levels in this query → keep empty
            variant.setInventoryList(new ArrayList<>());

            variants.add(variant);
        }

        product.setVariants(variants);

        return product;
    }

    private Long extractId(JsonNode gidNode) {
        if (gidNode == null || gidNode.isNull()) return null;
        String gid = gidNode.asText();
        return Long.parseLong(gid.substring(gid.lastIndexOf('/') + 1));
    }








}
