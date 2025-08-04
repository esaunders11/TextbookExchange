package com.esaunders.TextbookExchange.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esaunders.TextbookExchange.dtos.UpdateRequest;
import com.esaunders.TextbookExchange.repository.UserRepository;
import com.esaunders.TextbookExchange.service.UserService;

import lombok.AllArgsConstructor;

/**
 * Controller for user profile-related endpoints.
 * Handles updating the user's profile information.
 * @author Ethan Saunders
 */
@RestController
@RequestMapping("/api/users/profile")
@AllArgsConstructor
@CrossOrigin(origins = {"http://textbook-exchange-4ago.vercel.app", "http://localhost:3000", "http://localhost:4000"}, allowCredentials = "true")
public class ProfileController {

    /**
     * Repository for user data access.
     */
    private UserRepository userRepository;

    /**
     * Service for user-related operations.
     */
    private UserService userService;

    /**
     * Updates the authenticated user's profile with new first and/or last name.
     *
     * @param updateRequest the request containing updated profile fields
     * @return a response entity indicating success or error
     */
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateRequest updateRequest) {
        var user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        userRepository.save(user);
        return ResponseEntity.ok("Profile updated successfully");
    }

}
