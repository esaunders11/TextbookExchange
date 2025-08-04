package com.esaunders.TextbookExchange.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration for the application.
 * Configures JWT authentication, CORS, session management, and endpoint security.
 * @author Ethan Saunders
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Filter for processing JWT authentication.
     */
    private JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Constructor for injecting the JWT authentication filter.
     *
     * @param jwtAuthenticationFilter the JWT authentication filter
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        jwtAuthFilter = jwtAuthenticationFilter;
    }

    /**
     * Provides a BCrypt password encoder bean.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncorder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides the authentication manager bean.
     *
     * @param config the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures CORS for the application.
     *
     * @return the CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://textbook-exchange-six.vercel.app", "http://localhost:4000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Configures the security filter chain, including endpoint authorization,
     * session management, CORS, CSRF, and JWT filter.
     *
     * @param http the HTTP security builder
     * @return the security filter chain
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/api/books/**",
                    "/api/messages/**",
                    "/ws/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}