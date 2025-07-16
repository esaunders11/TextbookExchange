package com.esaunders.TextbookExchange.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esaunders.TextbookExchange.dtos.UpdateRequest;
import com.esaunders.TextbookExchange.repository.UserRepository;
import com.esaunders.TextbookExchange.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users/profile")
@AllArgsConstructor
public class ProfileController {
    private UserRepository userRepository;
    private UserService userService;

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
