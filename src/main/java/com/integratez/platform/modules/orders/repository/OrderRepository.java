package com.integratez.platform.modules.orders.repository;

import com.integratez.platform.modules.orders.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(Long orderId);

    List<Order> findAllByCreatedAtBetween(Instant startInclusive, Instant endExclusive);
}
