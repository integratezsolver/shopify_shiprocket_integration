# Integratez Multi-Channel Integration Platform - Architecture Guide

## Overview

This application is architected as a **modular monolith** designed to seamlessly transition into a **microservices architecture**. It supports integration with multiple sales channels (Shopify, WooCommerce, Amazon, etc.), fulfillment providers (Shiprocket, Delhivery, etc.), and ERP/EPL systems.

## Architecture Principles

### 1. **Module Isolation**
- Each channel/domain is a self-contained module
- Clear separation of concerns
- Minimal cross-module dependencies

### 2. **Adapter Pattern**
- All channels implement `ChannelProvider` interface
- All fulfillment providers implement `FulfillmentProviderAdapter`
- All ERP systems implement `ERPConnector`
- Enables pluggable implementations

### 3. **Event-Driven Communication**
- Domain events for loose coupling
- `DomainEvent` for inter-module communication
- Event listeners for async processing
- Future: Switch to RabbitMQ/Kafka for microservices

### 4. **Centralized API Gateway**
- `ChannelAPIGateway` for channel operations
- `FulfillmentAPIGateway` for fulfillment operations
- Single entry point for all integrations
- Easy routing to microservices later

### 5. **Shared Infrastructure**
- Resilience4j for retry/circuit breaker
- Structured logging for monitoring
- Trace ID for request tracing
- Standard exception handling

## Directory Structure

```
src/main/java/com/integratez/platform/
├── modules/
│   ├── core/                           # Shared interfaces, constants, DTOs
│   │   ├── adapter/
│   │   │   ├── ChannelProvider.java       # Sales channel contract
│   │   │   ├── FulfillmentProviderAdapter.java # Fulfillment contract
│   │   │   └── ERPConnector.java          # ERP integration contract
│   │   ├── constants/
│   │   │   ├── SalesChannel.java          # Supported channels enum
│   │   │   ├── FulfillmentProvider.java   # Fulfillment providers enum
│   │   │   ├── SyncDirection.java         # Sync directions
│   │   │   └── SyncStatus.java            # Sync status tracking
│   │   ├── dto/
│   │   │   ├── IntegrationResponse.java   # Standard API response
│   │   │   └── ChannelOperationResult.java # Operation tracking
│   │   └── exception/
│   │       ├── IntegrationException.java
│   │       └── ChannelOperationException.java
│   │
│   ├── events/                         # Event-driven architecture
│   │   ├── domain/
│   │   │   └── DomainEvent.java           # Base event class
│   │   ├── constants/
│   │   │   └── DomainEventType.java       # Event type constants
│   │   ├── publisher/
│   │   │   └── DomainEventPublisher.java  # Event publishing
│   │   └── listener/
│   │       └── DomainEventListener.java   # Event listener interface
│   │
│   ├── infrastructure/                 # Cross-cutting concerns
│   │   ├── config/
│   │   │   ├── ResilienceProperties.java  # Resilience config
│   │   │   └── Resilience4jConfig.java    # Retry, Circuit Breaker
│   │   ├── logging/
│   │   │   └── StructuredLog.java         # Structured logging
│   │   └── util/
│   │       └── TraceIdUtil.java           # Request tracing
│   │
│   ├── api/                            # API Gateway layer
│   │   └── gateway/
│   │       ├── ChannelAPIGateway.java     # Channel routing
│   │       └── FulfillmentAPIGateway.java # Fulfillment routing
│   │
│   ├── auth/                           # Authentication & Authorization
│   │   ├── controller/
│   │   ├── service/
│   │   ├── domain/
│   │   ├── repository/
│   │   ├── security/
│   │   ├── config/
│   │   └── dto/
│   │
│   ├── shopify/                        # Shopify channel adapter
│   │   ├── adapter/
│   │   │   └── ShopifyChannelAdapter.java # ChannelProvider impl
│   │   ├── controller/
│   │   ├── service/
│   │   ├── config/
│   │   ├── dto/
│   │   └── util/
│   │
│   ├── woocommerce/                    # WooCommerce channel (template)
│   │   ├── adapter/
│   │   │   └── WooCommerceChannelAdapter.java
│   │   ├── controller/
│   │   ├── service/
│   │   └── config/
│   │
│   ├── amazon/                         # Amazon channel (future)
│   │   └── ... (same structure)
│   │
│   ├── shiprocket/                     # Shiprocket fulfillment adapter
│   │   ├── adapter/
│   │   │   └── ShiprocketFulfillmentAdapter.java
│   │   ├── controller/
│   │   ├── service/
│   │   ├── config/
│   │   └── dto/
│   │
│   ├── erp/                            # ERP integrations
│   │   ├── adapter/
│   │   │   ├── SAPERPConnector.java       # SAP implementation
│   │   │   ├── OracleERPConnector.java    # Oracle (template)
│   │   │   └── ...
│   │   └── service/
│   │
│   ├── orders/                         # Order management (channel-agnostic)
│   │   ├── controller/
│   │   ├── service/
│   │   ├── domain/
│   │   ├── repository/
│   │   └── dto/
│   │
│   ├── product/                        # Product management (channel-agnostic)
│   │   ├── domain/
│   │   ├── repository/
│   │   ├── service/
│   │   └── dto/
│   │
│   ├── inventory/                      # Inventory management
│   │   ├── domain/
│   │   ├── service/
│   │   └── repository/
│   │
│   ├── sync/                           # Synchronization orchestration
│   │   ├── service/
│   │   │   ├── OrderSyncService.java
│   │   │   ├── ProductSyncService.java
│   │   │   └── InventorySyncService.java
│   │   ├── domain/
│   │   ├── listener/
│   │   └── job/
│   │
│   ├── webhooks/                       # Webhook handling
│   │   ├── controller/
│   │   ├── handler/
│   │   └── service/
│   │
│   └── common/                         # Shared models & utilities
│       ├── domain/
│       ├── repository/
│       ├── helper/
│       └── config/
│
└── IntegratezPlatformApplication.java
```

## Key Concepts

### 1. **Core Module - Shared Abstractions**

```java
// ChannelProvider.java - Contract for all channels
public interface ChannelProvider {
    SalesChannel getChannel();
    boolean authenticate(Map<String, String> credentials);
    ChannelOperationResult fetchOrders(Long integrationAccountId, Map<String, Object> filters);
    ChannelOperationResult fetchProducts(Long integrationAccountId, Map<String, Object> filters);
    // ... more methods
}
```

**Benefits:**
- New channels can be added without modifying existing code
- Loose coupling between channel implementations
- Easy to mock for testing
- Ready for microservices decomposition

### 2. **Adapter Pattern Usage**

```java
// ShopifyChannelAdapter.java
@Component
public class ShopifyChannelAdapter implements ChannelProvider {
    // Implements all ChannelProvider methods
}

// WooCommerceChannelAdapter.java
@Component
public class WooCommerceChannelAdapter implements ChannelProvider {
    // Implements all ChannelProvider methods for WooCommerce
}
```

All adapters are auto-discovered by Spring and available through `ChannelAPIGateway`.

### 3. **API Gateway Pattern**

```java
// ChannelAPIGateway.java - Single entry point
@Service
public class ChannelAPIGateway {
    private final List<ChannelProvider> channelProviders;
    
    public ChannelOperationResult fetchOrders(String channel, Long id, Map<String, Object> filters) {
        ChannelProvider provider = getChannelProvider(channel);
        return provider.fetchOrders(id, filters);
    }
}
```

**Advantages:**
- Single point of routing
- Easy to add cross-cutting concerns (logging, metrics, auth)
- Can switch to API Gateway microservice later

### 4. **Event-Driven Architecture**

```java
// Publishing an event from orders module
DomainEvent event = DomainEvent.create(
    DomainEventType.ORDER_CREATED,
    "orders",
    orderId,
    "Order",
    userId,
    integrationAccountId,
    orderData
);
domainEventPublisher.publish(event);

// Listening in sync module
@Component
public class OrderSyncListener implements DomainEventListener {
    @EventListener
    public void handleOrderCreated(DomainEvent event) {
        if (DomainEventType.ORDER_CREATED.equals(event.getEventType())) {
            // Sync order to ERP/channels
        }
    }
}
```

**Benefits:**
- Loose coupling between modules
- Async processing capability
- Future: Easy migration to message brokers

### 5. **Infrastructure - Resilience**

```java
// Configured with retry and circuit breaker
CircuitBreaker channelCircuitBreaker(CircuitBreakerRegistry registry) {
    // Fail fast if channel is down
    // Retry transient failures
}
```

## Scaling & Microservices Migration Path

### Phase 1: Current (Modular Monolith)
- All modules in single process
- Spring Events for communication
- Shared database

### Phase 2: Microservices (Minimal Changes Required)
```
Replace this:
├── Spring Events → RabbitMQ/Kafka (DomainEventPublisher)
├── API Gateway → Spring Cloud Gateway (dedicated service)
├── Database → Database per module (using saga pattern)
├── Each module → Separate microservice
```

**Why our architecture makes this easy:**
1. **Modules already decoupled** via adapter pattern
2. **Events already defined** in core module
3. **API Gateway already exists** (just extract to separate service)
4. **Interfaces allow remote calls** (REST/gRPC can replace local calls)

### Phase 3: Distributed System
- Each channel as microservice: `shopify-service`, `woocommerce-service`
- Each fulfillment as microservice: `shiprocket-service`, `delhivery-service`
- Each ERP connector as microservice: `sap-service`, `oracle-service`
- Central sync orchestrator
- API Gateway routing to microservices

## Adding New Channels

### Step 1: Create Channel Adapter
```java
package com.integratez.platform.modules.amazon.adapter;

@Component
public class AmazonChannelAdapter implements ChannelProvider {
    @Override
    public SalesChannel getChannel() {
        return SalesChannel.AMAZON;
    }
    // Implement all interface methods
}
```

### Step 2: Add to SalesChannel enum
```java
public enum SalesChannel {
    AMAZON("amazon", "Amazon"), // Added
}
```

### Step 3: Create services and controllers
The adapter is now available automatically through `ChannelAPIGateway`.

## Adding New Fulfillment Providers

### Step 1: Create Adapter
```java
package com.integratez.platform.modules.delhivery.adapter;

@Component
public class DelhiveryFulfillmentAdapter implements FulfillmentProviderAdapter {
    @Override
    public FulfillmentProvider getProvider() {
        return FulfillmentProvider.DELHIVERY;
    }
    // Implement all interface methods
}
```

### Step 2: Use through API Gateway
```java
fulfillmentGateway.createShipment("delhivery", integrationAccountId, shipmentData);
```

## Data Flow Examples

### Order Fetch & Sync Flow
```
1. REST API → ChannelController → OrderService
2. OrderService → ChannelAPIGateway.fetchOrders("shopify", ...)
3. Gateway → ShopifyChannelAdapter → ShopifyService → Shopify API
4. Response → Order saved to DB
5. Domain Event Published (ORDER_SYNCED)
6. OrderSyncListener (listening to event) → Forward to ERP/other channels
7. Response back to API
```

### Multi-Channel Product Sync
```
1. ProductSyncService triggered (scheduled or event)
2. For each enabled channel:
   - ChannelAPIGateway.fetchProducts(channel, ...)
   - Channel Adapter fetches from respective API
   - ProductService normalizes and saves
3. Domain Event published (PRODUCT_SYNCED)
4. Inventory sync listener triggers
5. Inventory updates sent to fulfillment providers
```

## Database Design for Microservices

Currently: Single database (normalized for all modules)

Future: Database per service
```
integratez_core:        # Shared
  - users
  - roles
  - integration_accounts

integratez_orders:      # Orders service
  - orders
  - order_items
  - customers

integratez_products:    # Products service
  - channel_products
  - channel_product_variants
  - channel_product_inventory

integratez_sync:        # Sync service
  - sync_logs
  - sync_batches
```

**Cross-service queries:** Use service-to-service APIs, not direct DB access.

## Monitoring & Observability

### Structured Logging
```java
StructuredLog log = StructuredLog.info(
    "shopify",
    "fetch_orders",
    "Fetched 100 orders from Shopify"
);
// Logs with traceId for correlation
```

### Metrics
- Operations per channel
- Success/failure rates
- Response times
- Queue depths (future)

### Tracing
- TraceId propagated across modules
- End-to-end request tracing
- Future: Jaeger/Zipkin integration

## Testing Strategy

### Unit Tests
- Test each adapter independently
- Mock interfaces

### Integration Tests
- Test with real DB
- Mock external APIs

### Contract Tests
- Test adapter contracts
- Ensure interface compliance

### E2E Tests
- Full flow testing
- Multiple channels

## Deployment

### Monolith Phase
```
docker build -t integratez .
docker run integratez
```

### Microservices Phase
```
docker build -f services/shopify/Dockerfile -t integratez-shopify .
docker build -f services/shiprocket/Dockerfile -t integratez-shiprocket .
// ... for each service
docker-compose up
```

## Key Files for Reference

- **Core Interfaces:** `modules/core/adapter/`
- **Domain Events:** `modules/events/`
- **Adapters:** `modules/{channel}/adapter/`, `modules/{provider}/adapter/`
- **API Gateways:** `modules/api/gateway/`
- **Configuration:** `modules/infrastructure/config/`

## Next Steps

1. **Implement remaining adapters** for WooCommerce, Amazon, etc.
2. **Create sync orchestrator** to coordinate multi-channel syncs
3. **Add event listeners** for inventory updates, fulfillment syncs
4. **Implement retry/failover** logic in adapters
5. **Add monitoring dashboards** for operations tracking
6. **Plan microservices split** when traffic grows

This architecture ensures you can scale horizontally and migrate to microservices without rewriting core logic.

