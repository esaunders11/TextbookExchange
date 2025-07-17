package com.esaunders.TextbookExchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.esaunders.TextbookExchange.dtos.CustomUserDetails;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.UserRepository;

@Service
public class Saml2UserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public Saml2UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
    
    // Placeholder for future SAML2 implementation
    public UserDetails loadUserBySaml2Authentication(Map<String, Object> saml2Attributes) {
        // Extract NC State Shibboleth attributes
        String unityId = getAttribute(saml2Attributes, "urn:oid:0.9.2342.19200300.100.1.1");
        String email = getAttribute(saml2Attributes, "urn:oid:0.9.2342.19200300.100.1.3");
        String firstName = getAttribute(saml2Attributes, "urn:oid:2.5.4.42");
        String lastName = getAttribute(saml2Attributes, "urn:oid:2.5.4.4");
        
        // Use unityId@ncsu.edu as email if email is not provided
        if (email == null || email.isEmpty()) {
            email = unityId + "@ncsu.edu";
        }
        
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        
        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Update user information from SAML attributes
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            user.setVerified(true); // SAML authenticated users are verified
            userRepository.save(user);
        } else {
            // Create new user
            user = createNewUser(unityId, email, firstName, lastName);
        }
        
        return new CustomUserDetails(user);
    }
    
    private String getAttribute(Map<String, Object> attributes, String name) {
        Object value = attributes.get(name);
        return value != null ? value.toString() : null;
    }
    
    private User createNewUser(String unityId, String email, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName != null ? firstName : "");
        user.setLastName(lastName != null ? lastName : "");
        user.setPassword(""); // No password for SAML users
        user.setVerified(true); // SAML authenticated users are verified
        
        return userRepository.save(user);
    }
    
    private List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }
}