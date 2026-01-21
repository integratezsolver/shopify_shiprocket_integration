package com.integratez.platform.modules.orders.domain;

import com.integratez.platform.modules.auth.domain.User;
import com.integratez.platform.modules.common.domain.IntegrationAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(name = "order_id")
    private Long orderId;      
    @Column(name = "order_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name = "integration_account_id")
    private Long integrationAccount;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "currency_code", length = 8)
    private String currencyCode;

    @Column(name = "fulfillment_status")
    private String fulfillmentStatus;

    @Column(name = "financial_status")
    private String financialStatus;

    @Column(name = "fully_paid")
    private Boolean fullyPaid;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "shipping_price")
    private BigDecimal shippingPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;
}
