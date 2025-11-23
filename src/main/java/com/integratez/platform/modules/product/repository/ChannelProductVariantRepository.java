package com.integratez.platform.modules.product.repository;



import com.integratez.platform.modules.product.domain.ChannelProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelProductVariantRepository extends JpaRepository<ChannelProductVariant, Long> {

    // Find all variants for a specific product
    List<ChannelProductVariant> findByProductId(Long productId);

    // Find all variants for a product inside a specific channel
    List<ChannelProductVariant> findByProductIdAndChannelId(Long productId, Long channelId);

    // Find a variant by Shopify variant_id + channel
    Optional<ChannelProductVariant> findByVariantIdAndChannelId(Long variantId, Long channelId);

    // Find variant by SKU (ERP uses this most)
    Optional<ChannelProductVariant> findBySkuAndChannelId(String sku, Long channelId);

    // Find all variants belonging to a channel
    List<ChannelProductVariant> findByChannelId(Long channelId);

    // To check if a variant already exists before saving
    boolean existsByVariantIdAndChannelId(Long variantId, Long channelId);

    // Find by inventory item ID (useful for inventory sync)
    Optional<ChannelProductVariant> findByInventoryItemId(Long inventoryItemId);
}

