package com.integratez.platform.modules.product.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "channel_product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelProductVariant {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(name = "product_id", nullable = false)
    private Long productId;       // Shopify product ID (matches products.product_id)

    @Column(name = "variant_id", nullable = false)
    private Long variantId;       // Shopify variant ID

    @Column(name = "inventory_item_id", nullable = false)
    private Long inventoryItemId; // Required for inventory sync

    private String sku;

    private String title;

    private Double price;


    // 🔥 Relationship: Product → Variants
    @OneToMany(mappedBy = "variantId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChannelProductInventory> inventoryList;
}
