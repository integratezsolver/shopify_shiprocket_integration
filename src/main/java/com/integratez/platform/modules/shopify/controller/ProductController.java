package com.integratez.platform.modules.shopify.controller;


import com.integratez.platform.modules.product.domain.ChannelProduct;
import com.integratez.platform.modules.shopify.service.ShopifyProductService;
import com.integratez.platform.modules.shopify.service.ShopifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {


    private  final ShopifyProductService shopifyProductService;

    private final com.integratez.platform.modules.shopify.service.ShopifyOrderService shopifyOrderService;


    @GetMapping("/{productId}")
    public ResponseEntity<?> syncProductFromShopify(
            @PathVariable String productId) {
        // Product ID must be a GID
        String gid = "gid://shopify/Product/" + productId;

        //ChannelProduct savedProduct = shopifyProductService.fetchAndSaveProduct(gid);

       List<ChannelProduct> products= shopifyProductService.fetchAndSaveAllProducts();

        return ResponseEntity.ok("Product synced with ID: " + " ");
    }


}
