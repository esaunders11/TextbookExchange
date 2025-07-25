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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esaunders.TextbookExchange.dtos.CustomUserDetails;
import com.esaunders.TextbookExchange.dtos.LoginRequest;
import com.esaunders.TextbookExchange.dtos.RegisterUser;
import com.esaunders.TextbookExchange.dtos.UserDto;
import com.esaunders.TextbookExchange.mapper.UserMapper;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.model.VerificationToken;
import com.esaunders.TextbookExchange.repository.UserRepository;
import com.esaunders.TextbookExchange.repository.VerificationTokenRepository;
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

    /** Repository for user data access. */
    private UserRepository userRepository;

    /** Service for user-related operations. */
    private UserService userService;

    /** Service for sending emails. */
    private EmailService emailService;

    /** Mapper for converting between User and UserDto. */
    private UserMapper userMapper;

    /** Authentication manager for processing authentication requests. */
    private AuthenticationManager authenticationManager;

    /** Repository for verification tokens. */
    private VerificationTokenRepository verificationTokenRepository;

    /** Password encoder for hashing user passwords. */
    private PasswordEncoder passwordEncoder;

    /** Service for JWT token operations. */
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
            if (!userDetails.isVerified()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not verified"));
            }
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
        VerificationToken token = new VerificationToken();

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            token.setToken(UUID.randomUUID().toString());
            token.setUser(user);
            token.setExpiryTime(LocalDateTime.now().plusHours(1));
            verificationTokenRepository.save(token);
        } catch (Exception e) {
            System.out.println("Failed to register user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }

        try {
            String verifyUrl = "http://localhost:3000/verify?token=" + token.getToken();
            emailService.sendEmail(user.getEmail(), "Verify your account", 
                "Copy the link in browser to verify: " + verifyUrl);
        } catch (Exception e) {
            System.out.println("Failed to send verification email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userMapper.toUserDto(user));
    }

    /**
     * Verifies a user using the provided token.
     *
     * @param token the verification token
     * @return a response entity indicating success or error
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        VerificationToken vToken = verificationTokenRepository.findByToken(token);
        if (vToken != null && !vToken.isExpired()) {
            User user = vToken.getUser();
            user.setVerified(true);
            userRepository.save(user);
            verificationTokenRepository.delete(vToken);
            return ResponseEntity.ok("Verified");
        }
        return ResponseEntity.badRequest().body("Invalid or expired token");
    }

    /**
     * Verifies the authenticated user and returns their user DTO.
     *
     * @return a response entity with the user DTO or unauthorized status
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUser() {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not verified");
        }
        UserDto userDto = userMapper.toUserDto(user);
        return ResponseEntity.ok(userDto);
    }
}
