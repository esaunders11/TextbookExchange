package com.esaunders.TextbookExchange.controller;

import java.util.HashMap;
import java.util.Map;

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
import com.esaunders.TextbookExchange.repository.UserRepository;
import com.esaunders.TextbookExchange.service.JwtService;
import com.esaunders.TextbookExchange.service.Saml2UserDetailsService;
import com.esaunders.TextbookExchange.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    private UserRepository userRepository;
    private UserService userService;
    private UserMapper userMapper;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private Saml2UserDetailsService saml2UserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
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

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterUser request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(null);
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userMapper.toUserDto(user));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser() {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        UserDto userDto = userMapper.toUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    // SAML2 endpoints - placeholder for future implementation
    @GetMapping("/saml2/login")
    public void saml2Login(HttpServletResponse response) throws Exception {
        // Placeholder: Will redirect to SAML2 login when implemented
        response.sendRedirect("https://shib.ncsu.edu/idp/shibboleth");
    }

    @PostMapping("/saml2/success")
    public ResponseEntity<?> saml2Success(HttpServletRequest request) {
        // Placeholder: Will handle SAML2 success callback when implemented
        return ResponseEntity.ok("SAML2 authentication placeholder");
    }

    @GetMapping("/saml2/failure")
    public ResponseEntity<?> saml2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("SAML2 authentication failed");
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSessionStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            response.put("authenticated", true);
            User user = userService.getAuthenticatedUser();
            if (user != null) {
                response.put("user", userMapper.toUserDto(user));
            }
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        return ResponseEntity.ok().build();
    }
}
