# Module Integration Guidelines

## Quick Reference for Developers

### Adding a New Sales Channel

#### 1. Create the Adapter
```
File: src/main/java/com/integratez/platform/modules/{channelname}/adapter/{ChannelName}ChannelAdapter.java

@Component
public class {ChannelName}ChannelAdapter implements ChannelProvider {
    
    private final {ChannelName}Service service;
    
    @Override
    public SalesChannel getChannel() {
        return SalesChannel.{CHANNELNAME};
    }
    
    @Override
    public ChannelOperationResult fetchOrders(Long integrationAccountId, Map<String, Object> filters) {
        // Implementation
    }
    // ... implement all interface methods
}
```

#### 2. Update Core Enum
```
File: src/main/java/com/integratez/platform/modules/core/constants/SalesChannel.java

Add to enum:
{CHANNELNAME}("{channelname}", "{Channel Display Name}"),
```

#### 3. Create Services
- `{ChannelName}Service.java` - Business logic
- `{ChannelName}Client.java` - API communication
- `{ChannelName}Properties.java` - Configuration

#### 4. Add Configuration
```
File: src/main/resources/application.yaml

integratez:
  channels:
    {channelname}:
      enabled: true
      api-url: ...
      api-version: ...
```

#### 5. Create Controller (Optional)
```
@RestController
@RequestMapping("/api/{channelname}")
public class {ChannelName}Controller {
    // Expose channel-specific endpoints
}
```

### Using the API Gateway

#### Channel Operations
```java
@Service
public class OrderService {
    
    private final ChannelAPIGateway channelGateway;
    
    public void syncAllChannels() {
        for (String channel : channelGateway.getSupportedChannels()) {
            ChannelOperationResult result = channelGateway.fetchOrders(
                channel,
                integrationAccountId,
                Map.of("pageSize", 50)
            );
            if (result.isSuccess()) {
                // Process orders
            }
        }
    }
}
```

#### Fulfillment Operations
```java
@Service
public class ShipmentService {
    
    private final FulfillmentAPIGateway fulfillmentGateway;
    
    public void createShipmentOnProvider(String provider, Map<String, Object> shipmentData) {
        ChannelOperationResult result = fulfillmentGateway.createShipment(
            provider,
            integrationAccountId,
            shipmentData
        );
    }
}
```

### Publishing Domain Events

#### From Any Module
```java
@Service
public class OrderService {
    
    private final DomainEventPublisher eventPublisher;
    
    public void createOrder(Order order) {
        // Save order
        
        // Publish event
        DomainEvent event = DomainEvent.create(
            DomainEventType.ORDER_CREATED,
            "orders",              // sourceModule
            order.getId().toString(),
            "Order",              // aggregateType
            currentUser.getId(),
            integrationAccountId,
            Map.of("orderId", order.getId())
        );
        
        eventPublisher.publish(event);
    }
}
```

#### Listening to Events
```java
@Component
public class InventorySyncListener {
    
    @EventListener
    public void handleProductUpdated(DomainEvent event) {
        if (DomainEventType.PRODUCT_SYNCED.equals(event.getEventType())) {
            // Update inventory based on product sync
            Map<String, Object> payload = event.getPayload();
            // Process...
        }
    }
}
```

### Error Handling

#### Use Custom Exceptions
```java
// For channel-specific errors
throw new ChannelOperationException(
    "Failed to fetch orders",
    "SHOPIFY_API_ERROR",
    "shopify",
    "fetch_orders",
    cause
);

// For general integration errors
throw new IntegrationException(
    "Failed to process order",
    "ORDER_PROCESSING_ERROR",
    cause
);
```

#### Handle in Controller
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ChannelOperationException.class)
    public ResponseEntity<IntegrationResponse<?>> handleChannelError(ChannelOperationException e) {
        return ResponseEntity.badRequest().body(
            IntegrationResponse.error(
                e.getMessage(),
                e.getErrorCode(),
                Map.of(
                    "channel", e.getChannel(),
                    "operation", e.getOperation()
                )
            )
        );
    }
}
```

### Async Processing with Events

#### Event Flow
```
1. Request → Service A
2. Service A publishes event
3. Service A returns response to client
4. Event processed asynchronously by listeners
5. Other services react to event

Benefits:
- Non-blocking operations
- Services don't wait for dependent services
- Easy to add new listeners later
```

#### Example: Product Sync
```java
// Product sync triggered
productSyncService.syncProducts("shopify");

// After sync completes, event published
// -> InventorySync listener picks it up
// -> Inventory updated automatically
// -> ShipmentReady listener picks it up
// -> Prepares shipment
// -> OrderNotification listener picks it up
// -> Notifies customer
```

### Tracing Requests

#### Using TraceId
```java
@Service
public class OrderService {
    
    public void processOrder(Long orderId) {
        String traceId = TraceIdUtil.getTraceId();
        // Use in logs for correlation
        log.info("Processing order {} with traceId {}", orderId, traceId);
        
        // All downstream services will have same traceId in thread context
        // Add to HTTP headers when calling other services
    }
}
```

#### In Controllers
```java
@RestController
public class OrderController {
    
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        String traceId = TraceIdUtil.generateTraceId();
        TraceIdUtil.setTraceId(traceId);
        
        try {
            // Process request
        } finally {
            TraceIdUtil.clear();
        }
    }
}
```

### Circuit Breaker & Retry

#### Automatic Retry
Configured in `Resilience4jConfig.java`:
- Max 3 attempts
- Exponential backoff: 1s, 2s, 4s
- Triggers on exceptions

#### Circuit Breaker
- Opens after 5 failures
- Waits 60 seconds
- Reopens automatically
- Prevents cascade failures

#### Configuration
```yaml
resilience4j:
  retry:
    instances:
      channel-retry:
        maxAttempts: 3
        waitDuration: 1000
  circuitbreaker:
    instances:
      channel-operations:
        failureRateThreshold: 50
        waitDurationInOpenState: 60000
```

## Folder Organization

### Each Channel Module Should Have:
```
modules/{channelname}/
├── adapter/
│   └── {ChannelName}ChannelAdapter.java
├── controller/
│   └── {ChannelName}Controller.java
├── service/
│   ├── {ChannelName}Service.java
│   ├── {ChannelName}Client.java
│   └── {ChannelName}Properties.java
├── dto/
│   ├── {ChannelName}OrderRequest.java
│   └── {ChannelName}ProductResponse.java
├── util/
│   ├── {ChannelName}Validator.java
│   └── {ChannelName}Queries.java
└── config/
    ├── WebClientConfig.java
    └── {ChannelName}Properties.java
```

### Each Domain Module (orders, products, etc.):
```
modules/{domainname}/
├── controller/
│   └── {DomainName}Controller.java
├── service/
│   └── {DomainName}Service.java
├── domain/
│   └── {DomainName}.java (JPA Entity)
├── repository/
│   └── {DomainName}Repository.java
└── dto/
    ├── {DomainName}Request.java
    └── {DomainName}Response.java
```

## Dependencies Between Modules

### Allowed:
- Any module → core (interfaces, constants, DTOs)
- Any module → infrastructure (logging, resilience)
- Channels → orders/products/inventory (to save data)
- Sync → all other modules (orchestrator)

### Avoid:
- Shopify → WooCommerce (channels are independent)
- Shiprocket → other fulfillment (providers are independent)
- Orders → Auth (auth is global, not domain-specific)

### Communication Methods:
1. **Same service:** Direct method calls
2. **Different modules:** Events (via DomainEventPublisher)
3. **Future microservices:** REST/gRPC APIs

## Testing Patterns

### Unit Tests
```java
@ExtendWith(MockitoExtension.class)
public class ShopifyChannelAdapterTest {
    
    @Mock
    private ShopifyService shopifyService;
    
    @InjectMocks
    private ShopifyChannelAdapter adapter;
    
    @Test
    public void testFetchOrders() {
        // Arrange
        when(shopifyService.fetchAllOrders(...)).thenReturn(orders);
        
        // Act
        ChannelOperationResult result = adapter.fetchOrders(...);
        
        // Assert
        assertTrue(result.isSuccess());
    }
}
```

### Integration Tests
```java
@SpringBootTest
@Import(TestContainers.class)
public class OrderServiceIntegrationTest {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Test
    public void testOrderSyncFlow() {
        // Create order via gateway
        // Verify event published
        // Verify listeners triggered
    }
}
```

## Common Gotchas

### 1. Cross-Module Coupling
❌ Bad: `shopifyService.fetchOrders()` → `orderService.saveOrder()` (tight coupling)
✅ Good: Shopify publishes ORDER_SYNCED event → OrderService listens (loose coupling)

### 2. Adapter Not Implementing Interface
❌ Bad: Create `ShopifyOrderService` without extending `ChannelProvider`
✅ Good: Always implement the interface for auto-discovery

### 3. Forgetting to Add to Enum
❌ Bad: Create adapter but forget to add to `SalesChannel` enum
✅ Good: Keep enums and adapters in sync

### 4. Not Handling Nulls
❌ Bad: Assume response fields are always present
✅ Good: Always check for nulls, provide defaults

### 5. Blocking Calls
❌ Bad: `synchronous REST call` inside event listener
✅ Good: Use async/events for long operations

## Performance Tips

1. **Pagination:** Always paginate API responses
2. **Caching:** Cache channel metadata and config
3. **Batch Operations:** Fetch multiple records in single API call
4. **Async:** Use events for non-blocking operations
5. **Connection Pooling:** Configure WebClient pool sizes
6. **Retry Budget:** Don't retry indefinitely

## Security Checklist

- [ ] Validate all input credentials
- [ ] Encrypt credentials at rest
- [ ] Use API keys from secure config (not hardcoded)
- [ ] Validate webhook signatures
- [ ] Rate limit API calls
- [ ] Log security events (authentication, authorization)
- [ ] Use HTTPS for external APIs
- [ ] Regular rotation of credentials

## Deployment Checklist

- [ ] All adapters registered as components
- [ ] Configuration properties loaded from `application.yaml`
- [ ] Resilience configs tuned for production
- [ ] Monitoring/alerting configured
- [ ] Database migrations run
- [ ] API keys configured per environment
- [ ] Webhook endpoints secured
- [ ] Rate limits appropriate for scale

## References

- **ARCHITECTURE.md** - Overall system design
- **Core Interfaces:** `modules/core/adapter/`
- **Event Types:** `modules/events/constants/DomainEventType.java`
- **API Gateways:** `modules/api/gateway/`

