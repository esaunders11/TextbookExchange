package com.esaunders.TextbookExchange.service;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (auth instanceof Saml2Authentication saml2Auth) {
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) saml2Auth.getPrincipal();
            String email = principal.getAttribute("urn:oid:0.9.2342.19200300.100.1.3")
                .stream()
                .findFirst()
                .map(Object::toString)
                .orElseThrow(() -> new RuntimeException("Email not found in SAML attributes"));
            
            return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        }

        throw new RuntimeException("Invalid authentication type");
    }

    public User getOrCreateSamlUser(Saml2Authentication saml2Auth) {
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) saml2Auth.getPrincipal();
        String email = principal.getAttribute("urn:oid:0.9.2342.19200300.100.1.3")
            .stream()
            .findFirst()
            .map(Object::toString)
            .orElseThrow(() -> new RuntimeException("Email not found in SAML attributes"));
        
        return userRepository.findByEmail(email)
            .orElseGet(() -> createSamlUser(principal));
    }
    
    private User createSamlUser(Saml2AuthenticatedPrincipal principal) {
        User user = new User();
        user.setEmail(getAttributeValue(principal, "urn:oid:0.9.2342.19200300.100.1.3")); // email
        user.setName(getAttributeValue(principal, "urn:oid:2.5.4.42")); // givenName
        user.setVerified(true);
        return userRepository.save(user);
    }
    
    private String getAttributeValue(Saml2AuthenticatedPrincipal principal, String attributeName) {
        return principal.getAttribute(attributeName)
            .stream()
            .findFirst()
            .map(Object::toString)
            .orElse(null);
    }
}