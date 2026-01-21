# Implementation Checklist & Dependencies

## Build Dependencies to Add

Add these to `build.gradle` for the new architecture:

```gradle
dependencies {
    // Existing dependencies...
    
    // Resilience4j - Circuit Breaker & Retry
    implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.1.0'
    implementation 'io.github.resilience4j:resilience4j-circuitbreaker:2.1.0'
    implementation 'io.github.resilience4j:resilience4j-retry:2.1.0'
    
    // Spring Cloud (for future microservices)
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.1'
    
    // RabbitMQ (for event broker, optional now, needed in Phase 2)
    // implementation 'org.springframework.boot:spring-boot-starter-amqp:3.0.0'
    
    // Micrometer for metrics
    implementation 'io.micrometer:micrometer-core:1.11.0'
    implementation 'io.micrometer:micrometer-tracing-bridge-brave:1.1.0'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test:3.0.0'
    testImplementation 'org.mockito:mockito-core:5.1.1'
    testImplementation 'io.rest-assured:rest-assured:5.3.1'
    testImplementation 'org.testcontainers:testcontainers:1.17.6'
}
```

## Application Configuration

Add to `application.yaml`:

```yaml
# Resilience4j Configuration
resilience4j:
  circuitbreaker:
    instances:
      channel-operations:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 60000
        failureRateThreshold: 50
        slowCallRateThreshold: 50
        slowCallDurationThreshold: 5000
        recordExceptions:
          - java.lang.Exception
        ignoreExceptions:
          - java.lang.IllegalArgumentException
  
  retry:
    instances:
      channel-retry:
        maxAttempts: 3
        waitDuration: 1000
        retryExceptions:
          - java.lang.Exception
        ignoreExceptions:
          - java.lang.IllegalArgumentException
        intervalFunction: exponential
        exponentialRandomizationFactor: 0.5
  
  timelimiter:
    instances:
      default:
        cancelRunningFuture: false
        timeoutDuration: 30000

# Logging
logging:
  level:
    com.integratez: DEBUG
    org.springframework: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

# Actuator endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,circuitbreakers,retries
  health:
    circuitbreakers:
      enabled: true
```

## File Implementation Checklist

### Core Module (DONE)
- [x] `SalesChannel.java` - Channel enumeration
- [x] `FulfillmentProvider.java` - Fulfillment provider enumeration
- [x] `SyncDirection.java` - Sync direction enumeration
- [x] `SyncStatus.java` - Sync status enumeration
- [x] `IntegrationResponse.java` - Standard API response
- [x] `ChannelOperationResult.java` - Operation tracking
- [x] `ChannelOperationException.java` - Channel operation exception
- [x] `IntegrationException.java` - Base integration exception
- [x] `ChannelProvider.java` - Channel contract interface
- [x] `FulfillmentProviderAdapter.java` - Fulfillment contract interface
- [x] `ERPConnector.java` - ERP contract interface

### Events Module (DONE)
- [x] `DomainEvent.java` - Base event class
- [x] `DomainEventType.java` - Event type constants
- [x] `DomainEventPublisher.java` - Event publisher service
- [x] `DomainEventListener.java` - Event listener interface

### Infrastructure Module (DONE)
- [x] `ResilienceProperties.java` - Resilience configuration properties
- [x] `Resilience4jConfig.java` - Circuit breaker & retry configuration
- [x] `StructuredLog.java` - Structured logging model
- [x] `TraceIdUtil.java` - Trace ID utility

### API Gateway Module (DONE)
- [x] `ChannelAPIGateway.java` - Channel API gateway
- [x] `FulfillmentAPIGateway.java` - Fulfillment API gateway

### Channel Adapters
- [x] `ShopifyChannelAdapter.java` - Shopify adapter (DONE)
- [ ] `WooCommerceChannelAdapter.java` - WooCommerce adapter (IN PROGRESS)
- [ ] `AmazonChannelAdapter.java` - Amazon adapter (TODO)
- [ ] `FlipkartChannelAdapter.java` - Flipkart adapter (TODO)

### Fulfillment Adapters
- [x] `ShiprocketFulfillmentAdapter.java` - Shiprocket adapter (DONE)
- [ ] `DelhiveryFulfillmentAdapter.java` - Delhivery adapter (TODO)
- [ ] `EKartFulfillmentAdapter.java` - eKart adapter (TODO)

### ERP Adapters
- [x] `SAPERPConnector.java` - SAP template (DONE)
- [ ] `OracleERPConnector.java` - Oracle template (TODO)
- [ ] `NetSuiteERPConnector.java` - NetSuite template (TODO)

### Documentation (DONE)
- [x] `ARCHITECTURE.md` - System architecture guide
- [x] `INTEGRATION_GUIDE.md` - Developer integration guide
- [x] `MICROSERVICES_ROADMAP.md` - Microservices migration plan
- [x] `DEPENDENCIES.md` - This file

### Next Priority Tasks
1. [ ] **Create Event Listeners**
   - OrderSyncListener (listens to ORDER_CREATED)
   - InventorySyncListener (listens to PRODUCT_SYNCED)
   - ShipmentListener (listens to ORDER_FULFILLED)

2. [ ] **Implement Channel Repositories**
   - Create JPA repositories for channel-specific entities
   - Add custom queries for channel data

3. [ ] **Implement Sync Services**
   - OrderSyncService (multi-channel order sync)
   - ProductSyncService (multi-channel product sync)
   - InventorySyncService (multi-channel inventory sync)

4. [ ] **Create REST Controllers**
   - OrderController (wrap ChannelAPIGateway)
   - ProductController (wrap ChannelAPIGateway)
   - SyncController (trigger sync operations)
   - FulfillmentController (wrap FulfillmentAPIGateway)

5. [ ] **Add Webhook Handlers**
   - ShopifyWebhookHandler (process Shopify webhooks)
   - WooCommerceWebhookHandler (process WC webhooks)
   - ShiprocketWebhookHandler (process shipment updates)

6. [ ] **Implement Tests**
   - Unit tests for all adapters
   - Integration tests for sync flows
   - Contract tests for adapter interfaces
   - E2E tests for critical paths

## Module Dependencies Map

```
┌──────────────���──────────────────────────────────────────────┐
│                     REST API Layer                           │
│  (Controllers: Order, Product, Sync, Fulfillment)           │
└────────────────┬────────────────────────────────────────────┘
                 │
┌────────────────▼─────────────────────────────────────────────┐
│                  API Gateway Layer                            │
│  ┌──────────────────┐      ┌──────────────────────┐          │
│  │ChannelAPIGateway │      │FulfillmentAPIGateway │          │
│  └────────┬─────────┘      └──────────┬───────────┘          │
└───────────┼──────────────────────────┼────────────────────────┘
            │                          │
┌───────────┴──────────────┬───────────┴──────────────────┐
│                          │                              │
├──────────────────────────┼──────────────────────────────┤
│     Channel Adapters     │  Fulfillment Adapters        │
├──────────────────────────┼──────────────────────────────┤
│ ┌─────────────────────┐  │ ┌─────────────────────────┐ │
│ │ ShopifyAdapter      │  │ │ ShiprocketAdapter       │ │
│ │ WooCommerceAdapter  │  │ │ DelhiveryAdapter        │ │
│ │ AmazonAdapter       │  │ │ eKartAdapter            │ │
│ └─────────────────────┘  │ └─────────────────────────┘ │
├──────────────────────────┼──────────────────────────────┤
│      Service Layer       │      ERP Connectors          │
├──────────────────────────┼──────────────────────────────┤
│ ┌─────────────────────┐  │ ┌─────────────────────────┐ │
│ │ ShopifyService      │  │ │ SAPConnector            │ │
│ │ OrderService        │  │ │ OracleConnector         │ │
│ │ ProductService      │  │ │ NetSuiteConnector       │ │
│ │ SyncService         │  │ │ DynamicsConnector       │ │
│ └─────────────────────┘  │ └─────────────────────────┘ │
├──────────────────────────┼──────────────────────────────┤
│     Repository Layer     │    Event Infrastructure      │
├──────────────────────────┼──────────────────────────────┤
│ ┌─────────────────────┐  │ ┌─────────────────────────┐ │
│ │ OrderRepository     │  │ │ DomainEventPublisher    │ │
│ │ ProductRepository   │  │ │ DomainEventListeners    │ │
│ │ InventoryRepository │  │ │ Event Handlers          │ │
│ └─────────────────────┘  │ └─────────────────────────┘ │
├──────────────────────────┴──────────────────────────────┤
│          Shared Infrastructure Layer                     │
├────────────────────────────────────────────────────────────┤
│ ┌──────────────────┐  ┌──────────────────────────────┐    │
│ │ ResilienceConfig │  │ StructuredLogging, TraceId   │    │
│ │ (Circuit Breaker,│  │ Exception Handling           │    │
│ │  Retry, Timeout) │  │ Security                     │    │
│ └──────────────────┘  └──────────────────────────────┘    │
├────────────────────────────────────────────────────────────┤
│          Core Module (Interfaces & Constants)             │
├────────────────────────────────────────────────────────────┤
│ ChannelProvider, FulfillmentProviderAdapter, ERPConnector  │
│ SalesChannel, FulfillmentProvider, SyncDirection/Status    │
│ IntegrationResponse, ChannelOperationResult, Exceptions    │
├────────────────────────────────────────────────────────────┤
│                    Database Layer                          │
├────────────────────────────────────────────────────────────┤
│ MySQL: All entities (single DB now, split in Phase 2)      │
└────────────────────────────────────────────────────────────┘
```

## Adapter Implementation Template

For each new adapter, follow this template:

```java
/**
 * {ChannelName} Channel Adapter
 * 
 * Path: modules/{modulename}/adapter/{ClassName}ChannelAdapter.java
 * 
 * Implements: ChannelProvider
 * 
 * Dependencies:
 * - {ChannelName}Service
 * - {ChannelName}Properties
 * - DomainEventPublisher
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class {ClassName}ChannelAdapter implements ChannelProvider {
    
    private final {ClassName}Service service;
    private final DomainEventPublisher eventPublisher;
    
    @Override
    public SalesChannel getChannel() {
        return SalesChannel.{ENUMNAME};
    }
    
    // Implement all interface methods
}
```

## Testing Template

```java
/**
 * Tests for {ChannelName} Adapter
 * 
 * Path: src/test/java/com/integratez/platform/modules/{modulename}/adapter/
 */

@ExtendWith(MockitoExtension.class)
public class {ClassName}ChannelAdapterTest {
    
    @Mock
    private {ClassName}Service service;
    
    @Mock
    private DomainEventPublisher eventPublisher;
    
    @InjectMocks
    private {ClassName}ChannelAdapter adapter;
    
    @Test
    public void testGetChannel() {
        assertEquals(SalesChannel.{ENUMNAME}, adapter.getChannel());
    }
    
    @Test
    public void testFetchOrders_Success() {
        // Test successful order fetch
    }
    
    @Test
    public void testFetchOrders_Failure() {
        // Test failure handling
    }
}
```

## Configuration Template

```yaml
# application-{modulename}.yaml
integratez:
  channels:
    {modulename}:
      enabled: true
      api-url: ${API_URL}
      api-version: ${API_VERSION}
      timeout-ms: 30000
      retry:
        maxAttempts: 3
        initialDelayMs: 1000
      rateLimit:
        requestsPerSecond: 50
```

## Common Implementation Patterns

### 1. Adapter with Service
```
Adapter (implements ChannelProvider)
    └── Service (business logic)
        └── Client (API communication)
```

### 2. Event Publishing
```
Service saves entity
    └── Publishes DomainEvent
        └── Listeners process event async
```

### 3. Error Handling
```
Try {
    API Call
} Catch {
    ChannelOperationException (specific error)
        └── Logged with TraceId
        └── Published as SYNC_FAILED event
}
```

### 4. Pagination
```
Do {
    Fetch page of results
    Process results
    Publish events for each
} Until (no more pages)
```

## Validation Checklist Before Production

- [ ] All adapters implement interface completely
- [ ] Error handling for all failure cases
- [ ] Retry logic for transient failures
- [ ] Circuit breaker for service resilience
- [ ] Structured logging throughout
- [ ] Event publishing for domain changes
- [ ] Database transactions properly handled
- [ ] API keys secured in config
- [ ] Rate limits respected
- [ ] Webhook signatures verified
- [ ] Tests passing (unit, integration, E2E)
- [ ] Documentation updated
- [ ] Performance benchmarked
- [ ] Security review completed
- [ ] Monitoring/alerts configured

## Helpful Commands

### Build Project
```bash
./gradlew clean build
```

### Run Tests
```bash
./gradlew test
```

### Run Specific Test
```bash
./gradlew test --tests "*ShopifyChannelAdapterTest"
```

### Check Code Quality
```bash
./gradlew checkstyleMain
```

### Run Application
```bash
./gradlew bootRun
```

## Resources

- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **Resilience4j:** https://resilience4j.readme.io/
- **Spring Cloud:** https://spring.io/projects/spring-cloud
- **JPA/Hibernate:** https://spring.io/projects/spring-data-jpa
- **Testing:** https://spring.io/guides/gs/testing-web/

## Support & Questions

1. **Architecture questions?** → Refer to `ARCHITECTURE.md`
2. **Implementation help?** → Refer to `INTEGRATION_GUIDE.md`
3. **Microservices planning?** → Refer to `MICROSERVICES_ROADMAP.md`
4. **Adapter templates?** → Check `WooCommerceChannelAdapter.java`
5. **Event patterns?** → Check event listener examples

---

**Last Updated:** 2024
**Architecture Version:** 1.0 (Modular Monolith, Microservices Ready)
**Next Review:** After Phase 1 completion

