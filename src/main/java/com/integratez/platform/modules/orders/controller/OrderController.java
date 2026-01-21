package com.integratez.platform.modules.orders.controller;

import com.integratez.platform.modules.auth.domain.User;
import com.integratez.platform.modules.auth.repository.UserRepository;
import com.integratez.platform.modules.auth.service.CustomUserDetailsService;
import com.integratez.platform.modules.orders.domain.Order;
import com.integratez.platform.modules.orders.service.OrderMapperService;
import com.integratez.platform.modules.orders.service.OrderService;
import com.integratez.platform.modules.shopify.dto.OrderFilterRequest;
import com.integratez.platform.modules.shopify.service.ShopifyPaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ShopifyPaginationService paginationService;
    private final OrderMapperService orderMapperService;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    /**
     * Fetch order from Shopify by numeric id, persist, and return the saved record.
     */
    @GetMapping("/sync/{id}")
    public ResponseEntity<Order> syncOrderById(@PathVariable String id) {
        String orderGid = "gid://shopify/Order/" + id;
        Order saved = orderService.syncOrderById(orderGid);
        return ResponseEntity.ok(saved);
    }

    /**
     * Get an order from DB by Shopify numeric id (no Shopify call).
     */


    @PostMapping("/by-date")
    public ResponseEntity<?> syncOrdersByDate(
            @RequestBody OrderFilterRequest filter
    ) {

        String shopifyQuery = "created_at:>=" + filter.getStartDate()
                + " AND created_at:<=" + filter.getEndDate();

        // Fetch all orders from Shopify (with pagination)
        List<Map<String, Object>> allOrdersJson =
                paginationService.fetchAllOrders(shopifyQuery, filter.getLimit());

        String username = userDetailsService.getAuthenticatedUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No authenticated user found!"));
        // Save all orders one-by-one
        List<Order> savedOrders =
                orderService.syncOrderByDate(allOrdersJson, currentUser);

        return ResponseEntity.ok(savedOrders);
    }

}
