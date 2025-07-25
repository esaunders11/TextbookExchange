package com.esaunders.TextbookExchange.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); 
        config.setApplicationDestinationPrefixes("/app"); 
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        System.out.println("========== Registering STOMP endpoints for WebSocket ==========");
        
        registry.addEndpoint("/ws")
            .setAllowedOrigins("https://textbook-exchange-six.vercel.app", "http://localhost:3000")
            .withSockJS();
            
        System.out.println("========== WebSocket endpoint registered successfully ==========");
    }
}
