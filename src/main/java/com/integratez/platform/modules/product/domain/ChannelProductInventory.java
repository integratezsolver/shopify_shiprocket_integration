package com.integratez.platform.modules.product.domain;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "channel_product_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelProductInventory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(name = "variant_id", nullable = false)
    private Long variantId;          // Shopify variant ID

    @Column(name = "inventory_item_id")
    private Long inventoryItemId;    // Shopify inventory item ID (required)

    @Column(name = "location_id", nullable = false)
    private String locationId;       // Shopify location/warehouse ID

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "variant_row_id")
    private ChannelProductVariant variant;
}

