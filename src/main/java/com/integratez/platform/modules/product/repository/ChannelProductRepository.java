package com.integratez.platform.modules.product.repository;


import com.integratez.platform.modules.product.domain.ChannelProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelProductRepository extends JpaRepository<ChannelProduct, Long> {

    Optional<ChannelProduct> findByProductIdAndChannelId(Long productId, Long channelId);

    List<ChannelProduct> findAllByChannelId(Long channelId);

    boolean existsByProductIdAndChannelId(Long productId, Long channelId);
}
