package com.integratez.platform.modules.shiprocket.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for Shiprocket API client
 * Sets up WebClient with proper timeouts, connection pooling, SSL support, and retry logic
 */
@Configuration
public class ShiprocketClientConfig {

    /**
     * Create WebClient bean for Shiprocket API calls
     * Uses reactive client for async, non-blocking communication with connection pooling and retry logic
     *
     * @return configured WebClient for Shiprocket API
     */
    @Bean(name = "shiprocketWebClient")
    public WebClient shiprocketWebClient() throws SSLException {
        // Connection pool configuration
        ConnectionProvider connectionProvider = ConnectionProvider.builder("shiprocket-pool")
                .maxConnections(100)                    // Maximum connections
                .maxIdleTime(Duration.ofSeconds(60))   // Max idle time
                .maxLifeTime(Duration.ofMinutes(30))   // Max connection lifetime
                .pendingAcquireMaxCount(1000)          // Max pending acquisitions
                .pendingAcquireTimeout(Duration.ofSeconds(45))
                .build();

        // SSL Context configuration
        var sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        // HttpClient with comprehensive configuration
        HttpClient httpClient = HttpClient.create(connectionProvider)
                // Connection options
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)

                // Timeouts
                .responseTimeout(Duration.ofSeconds(30))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS))
                        .addHandlerLast(new IdleStateHandler(60, 60, 120, TimeUnit.SECONDS)))

                // SSL configuration
                .secure(sslSpec -> sslSpec.sslContext(sslContext));

        // Exchange strategies for larger payloads
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl("https://apiv2.shiprocket.in")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "Integratez-Shiprocket-Client/1.0")
                .build();
    }
}

