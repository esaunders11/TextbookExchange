package com.esaunders.TextbookExchange.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for STOMP messaging and endpoint registration.
 * Enables message broker and sets up allowed origins for SockJS.
 * @author Ethan Saunders
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker for STOMP messaging.
     * Sets the topic for outgoing messages and application prefix for incoming messages.
     * @param config the message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Enable simple broker for outgoing messages
        config.setApplicationDestinationPrefixes("/app"); // Set prefix for incoming messages
    }

    /**
     * Registers the STOMP WebSocket endpoint and configures allowed origins.
     * @param registry the STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        System.out.println("========== Registering STOMP endpoints for WebSocket ==========");
        registry.addEndpoint("/ws")
            .setAllowedOrigins("https://textbook-exchange-six.vercel.app", "http://localhost:3000")
            .withSockJS();
        System.out.println("========== WebSocket endpoint registered successfully ==========");
    }
}
