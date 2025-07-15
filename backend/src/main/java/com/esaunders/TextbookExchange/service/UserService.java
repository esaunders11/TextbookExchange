package com.esaunders.TextbookExchange.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esaunders.TextbookExchange.dtos.CustomUserDetails;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {
    private UserRepository userRepository;

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
