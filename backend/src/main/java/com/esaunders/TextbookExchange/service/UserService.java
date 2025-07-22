package com.esaunders.TextbookExchange.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esaunders.TextbookExchange.dtos.CustomUserDetails;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.UserRepository;

import lombok.AllArgsConstructor;

/**
 * Service for user-related operations.
 * Handles retrieval of the authenticated user from the security context.
 * @author Ethan Saunders
 */
@AllArgsConstructor
@Service
public class UserService {
    /** Repository for accessing user data. */
    private UserRepository userRepository;

    /**
     * Retrieves the currently authenticated user from the security context.
     * @return the authenticated User
     * @throws RuntimeException if the user is unauthenticated or invalid
     */
    @Transactional
    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails customUser) {
            User user = userRepository.findById(customUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            return user;
        } else {
            throw new RuntimeException("Invalid user");
        }
    }
}
