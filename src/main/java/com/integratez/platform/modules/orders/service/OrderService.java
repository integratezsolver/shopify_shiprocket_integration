package com.integratez.platform.modules.orders.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integratez.platform.modules.auth.domain.User;
import com.integratez.platform.modules.auth.repository.UserRepository;
import com.integratez.platform.modules.auth.service.CustomUserDetailsService;
import com.integratez.platform.modules.orders.domain.Address;
import com.integratez.platform.modules.orders.domain.Customer;
import com.integratez.platform.modules.orders.domain.Order;
import com.integratez.platform.modules.orders.domain.OrderItem;
import com.integratez.platform.modules.orders.repository.CustomerRepository;
import com.integratez.platform.modules.orders.repository.OrderRepository;
import com.integratez.platform.modules.shopify.util.ShopifyQueries;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class OrderService {

    private final WebClient shopifyWebClient;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    public OrderService(
            @Qualifier("webClient") WebClient shopifyWebClient,
            ObjectMapper objectMapper,
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            CustomUserDetailsService userDetailsService) {
        this.shopifyWebClient = shopifyWebClient;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }


    /**
     * Fetch an order from Shopify by GID, map it, and persist to DB.
     */
    public Order syncOrderById(String orderGid) {

        String username = userDetailsService.getAuthenticatedUsername();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No authenticated user found!"));
        Map<String, Object> variables = Map.of("id", orderGid);
        String response = execute(ShopifyQueries.GET_ORDER_BY_ID, variables);
        return mapAndSaveOrder(response, currentUser);
    }


    public List<Order> syncOrderByDate(List<Map<String, Object>> ordersJson, User currentUser) {

        List<Order> saved = new ArrayList<>();

        for (Map<String, Object> orderMap : ordersJson) {

            JsonNode orderNode = objectMapper.valueToTree(orderMap);
            Order savedOrder = saveSingleOrder(orderNode, currentUser);

            if (savedOrder != null) {
                saved.add(savedOrder);
            }
        }

        return saved;
    }



    public Order saveSingleOrder(JsonNode orderNode, User currentUser) {

        if (orderNode == null || orderNode.isMissingNode()) return null;

        Long orderId = extractNumericId(orderNode.path("id").asText());

        // If order already stored → return existing
        Optional<Order> existing = orderRepository.findByOrderId(orderId);
        if (existing.isPresent()) return existing.get();

        Order order = buildOrder(orderNode, currentUser);

        // Link items to the order
        order.getItems().forEach(item -> item.setOrder(order));

        // Customer: reuse if exists
        if (order.getCustomer() != null && order.getCustomer().getCustomerId() != null) {
            Customer existingCustomer =
                    customerRepository.findByCustomerId(order.getCustomer().getCustomerId())
                            .orElse(null);

            if (existingCustomer != null) {
                order.setCustomer(existingCustomer);
            }
        }

        return orderRepository.save(order);
    }




    public String execute(String query, Map<String, Object> variables) {

        Map<String, Object> body = Map.of(
                "query", query,
                "variables", variables
        );

        return shopifyWebClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private Order mapAndSaveOrder(String responseBody, User currentUser) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode orderNode = root.path("data").path("order");
            if (orderNode.isMissingNode() || orderNode.isNull()) {
                throw new IllegalStateException("Order not found in Shopify response");
            }

            Order order = buildOrder(orderNode, currentUser);

            // link items back to order
            order.getItems().forEach(item -> item.setOrder(order));

            // persist (customer reused if already exists)
            if (order.getCustomer() != null && order.getCustomer().getCustomerId() != null) {
                Customer customer = customerRepository
                        .findByCustomerId(order.getCustomer().getCustomerId())
                        .orElse(order.getCustomer());
                order.setCustomer(customer);
            }

            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map/save Shopify order", e);
        }
    }

    private Order buildOrder(JsonNode orderNode, User currentUser) {
        String idStr = orderNode.path("id").asText(); // gid://shopify/Order/6940036268125
        Long numericOrderId = extractNumericId(idStr);

        Order.OrderBuilder builder = Order.builder()
                .orderId(numericOrderId)
                .user(currentUser)
                .integrationAccount(1L)
                .name(orderNode.path("name").asText(null))
                .createdAt(parseInstant(orderNode.path("createdAt").asText(null)))
                .currencyCode(orderNode.path("currencyCode").asText(null))
                .fulfillmentStatus(orderNode.path("displayFulfillmentStatus").asText(null))
                .financialStatus(orderNode.path("displayFinancialStatus").asText(null))
                .fullyPaid(orderNode.path("fullyPaid").asBoolean(false))
                .cancelReason(orderNode.path("cancelReason").asText(null))
                .cancelledAt(parseInstant(orderNode.path("cancelledAt").asText(null)))
                .totalPrice(extractMoney(orderNode.path("totalPriceSet")))
                .shippingPrice(extractMoney(orderNode.path("totalShippingPriceSet")));

        // customer
        JsonNode customerNode = orderNode.path("customer");
        if (!customerNode.isMissingNode() && !customerNode.isNull()) {
            builder.customer(buildCustomer(customerNode));
        }

        // addresses
        JsonNode shippingAddressNode = orderNode.path("shippingAddress");
        if (!shippingAddressNode.isMissingNode() && !shippingAddressNode.isNull()) {
            builder.shippingAddress(buildAddress(shippingAddressNode, "SHIPPING"));
        }
        JsonNode billingAddressNode = orderNode.path("billingAddress");
        if (!billingAddressNode.isMissingNode() && !billingAddressNode.isNull()) {
            builder.billingAddress(buildAddress(billingAddressNode, "BILLING"));
        }

        // items
        List<OrderItem> items = new ArrayList<>();
        JsonNode lineItems = orderNode.path("lineItems").path("nodes");
        if (lineItems.isArray()) {
            lineItems.forEach(node -> items.add(buildItem(node)));
        }
        builder.items(items);

        return builder.build();
    }

    private Customer buildCustomer(JsonNode customerNode) {
        JsonNode idNode = customerNode.path("id"); // gid://shopify/Customer/123
        Long numericId = extractNumericId(idNode.asText(null));

        return Customer.builder()
                .customerId(numericId)
                .email(customerNode.path("email").asText(null))
                .firstName(customerNode.path("firstName").asText(null))
                .lastName(customerNode.path("lastName").asText(null))
                .phone(customerNode.path("phone").asText(null))
                .build();
    }

    private Address buildAddress(JsonNode addressNode, String type) {
        return Address.builder()
                .firstName(addressNode.path("firstName").asText(null))
                .lastName(addressNode.path("lastName").asText(null))
                .company(addressNode.path("company").asText(null))
                .address1(addressNode.path("address1").asText(null))
                .address2(addressNode.path("address2").asText(null))
                .city(addressNode.path("city").asText(null))
                .province(addressNode.path("province").asText(null))
                .country(addressNode.path("country").asText(null))
                .zip(addressNode.path("zip").asText(null))
                .phone(addressNode.path("phone").asText(null))
                .addressType(type)
                .build();
    }

    private OrderItem buildItem(JsonNode itemNode) {
        return OrderItem.builder()
                .lineItemId(extractNumericId(itemNode.path("id").asText(null)))
                .title(itemNode.path("name").asText(null))
                .variantTitle(itemNode.path("variantTitle").asText(null))
                .sku(itemNode.path("sku").asText(null))
                .quantity(itemNode.path("quantity").asInt(0))
                .discountedTotal(extractMoney(itemNode.path("discountedTotalSet")))
                .originalTotal(extractMoney(itemNode.path("originalTotalSet")))
                .build();
    }

    private Long extractNumericId(String gid) {
        if (gid == null) return null;
        int idx = gid.lastIndexOf('/');
        if (idx == -1 || idx == gid.length() - 1) {
            return null;
        }
        try {
            return Long.parseLong(gid.substring(idx + 1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Instant parseInstant(String value) {
        return value == null || value.isEmpty() ? null : Instant.parse(value);
    }

    private BigDecimal extractMoney(JsonNode moneySetNode) {
        JsonNode amount = moneySetNode.path("shopMoney").path("amount");
        return amount.isMissingNode() || amount.isNull() || amount.asText().isEmpty()
                ? null
                : new BigDecimal(amount.asText());
    }
}
