package com.esaunders.TextbookExchange.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.esaunders.TextbookExchange.dtos.CustomUserDetails;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.UserRepository;

/**
 * Service for loading user-specific data for authentication.
 * Implements Spring Security's UserDetailsService.
 * @author Ethan Saunders
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    /** Repository for accessing user data. */
    private final UserRepository userRepository;

    /**
     * Constructs the service with the required user repository.
     * @param userRepository the user repository
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user by email for authentication.
     * @param email the user's email address
     * @return the UserDetails for authentication
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

}
