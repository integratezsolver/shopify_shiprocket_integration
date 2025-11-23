package com.integratez.platform.modules.product.repository;

import com.integratez.platform.modules.product.domain.ChannelProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelProductInventoryRepository extends JpaRepository<ChannelProductInventory, Long> {

    // Get all inventory rows for a variant (across locations)
    List<ChannelProductInventory> findByVariantId(Long variantId);

    // Get inventory for a variant for a specific channel
    List<ChannelProductInventory> findByVariantIdAndChannelId(Long variantId, Long channelId);

    // Get inventory for a specific inventory_item_id (Shopify)
    List<ChannelProductInventory> findByInventoryItemId(Long inventoryItemId);

    // Get specific inventory level for a location (used for ERP → Shopify update)
    Optional<ChannelProductInventory> findByInventoryItemIdAndLocationId(Long inventoryItemId, String locationId);

    // Fetch all inventory rows for a channel
    List<ChannelProductInventory> findByChannelId(Long channelId);

    // Check if an inventory row already exists (avoid duplicates)
    boolean existsByInventoryItemIdAndLocationId(Long inventoryItemId, String locationId);
}