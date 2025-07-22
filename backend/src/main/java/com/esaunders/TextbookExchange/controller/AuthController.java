package com.esaunders.TextbookExchange.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esaunders.TextbookExchange.dtos.CustomUserDetails;
import com.esaunders.TextbookExchange.dtos.LoginRequest;
import com.esaunders.TextbookExchange.dtos.RegisterUser;
import com.esaunders.TextbookExchange.dtos.UserDto;
import com.esaunders.TextbookExchange.mapper.UserMapper;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.model.VerificationToken;
import com.esaunders.TextbookExchange.repository.UserRepository;
import com.esaunders.TextbookExchange.repository.VerficationTokenRepository;
import com.esaunders.TextbookExchange.service.EmailService;
import com.esaunders.TextbookExchange.service.JwtService;
import com.esaunders.TextbookExchange.service.UserService;

import lombok.AllArgsConstructor;

/**
 * Controller for authentication-related endpoints.
 * Handles login, registration, and user verification.
 * @author Ethan Saunders
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"https://textbook-exchange-six.vercel.app", "http://localhost:3000"}, allowCredentials = "true")
public class AuthController {

    /**
     * Repository for user data access.
     */
    private UserRepository userRepository;

    /**
     * Service for user-related operations.
     */
    private UserService userService;

    /**
     * Service for sending emails.
     */
    private EmailService emailService;

    /**
     * Mapper for converting between User and UserDto.
     */
    private UserMapper userMapper;

    /**
     * Authentication manager for processing authentication requests.
     */
    private AuthenticationManager authenticationManager;

    /**
     * Repository for verification tokens.
     */
    private VerficationTokenRepository verificationTokenRepository;

    /**
     * Password encoder for hashing user passwords.
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Service for JWT token operations.
     */
    private JwtService jwtService;

    /**
     * Authenticates a user and returns a JWT token if successful.
     *
     * @param loginRequest the login request containing email and password
     * @return a response entity with the JWT token or unauthorized status
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtService.generateToken(userDetails);
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Registers a new user if the email is not already taken.
     *
     * @param request the registration request containing user details
     * @return a response entity with the created user DTO or conflict status
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterUser request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(null);
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryTime(LocalDateTime.now().plusHours(1));
        verificationTokenRepository.save(token);

        String verifyUrl = "http://localhost:3000/verify?token=" + token;
        emailService.sendEmail(user.getEmail(), "Verify your account", 
            "Click the link to verify: " + verifyUrl);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userMapper.toUserDto(user));
    }

    /**
     * Verifies the authenticated user and returns their user DTO.
     *
     * @return a response entity with the user DTO or unauthorized status
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser() {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        UserDto userDto = userMapper.toUserDto(user);
        return ResponseEntity.ok(userDto);
    }
}
