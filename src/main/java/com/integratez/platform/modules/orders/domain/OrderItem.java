package com.integratez.platform.modules.orders.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g., gid://shopify/LineItem/16093777690717

    @Column(name = "line_item_id")
    private Long lineItemId;        // Numeric portion of line item ID

    @Column(name = "title")
    private String title;

    @Column(name = "variant_title")
    private String variantTitle;

    @Column(name = "sku")
    private String sku;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discounted_total")
    private BigDecimal discountedTotal;

    @Column(name = "original_total")
    private BigDecimal originalTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
