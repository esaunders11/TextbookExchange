package com.esaunders.TextbookExchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Textbook Exchange system.
 * Bootstraps the Spring Boot application.
 */
@SpringBootApplication
public class TextbookExchangeApplication {

    /**
     * Main method to start the Spring Boot application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(TextbookExchangeApplication.class, args);
    }

}
