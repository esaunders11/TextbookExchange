package com.esaunders.TextbookExchange.dtos;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.esaunders.TextbookExchange.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Custom implementation of UserDetails for Spring Security.
 * Wraps the application's User entity for authentication and authorization.
 * @author Ethan Saunders
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    /** The user entity associated with these details. */
    private final User user;

    /**
     * Returns the authorities granted to the user.
     * 
     * @return an empty list of authorities
     */
    @Override
    public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * Returns the user's password.
     * 
     * @return the user's password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the user's username (email).
     * 
     * @return the user's email
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user is verified.
     * 
     * @return true if the user is verified, false otherwise
     */
    public boolean isVerified() {
        return user.isVerified();
    }

    /**
     * Returns the user's unique ID.
     * 
     * @return the user's ID
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Indicates whether the user's account has expired.
     * 
     * @return true if the account is non-expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * 
     * @return true if the account is non-locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     * 
     * @return true if credentials are non-expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * 
     * @return true if the user is enabled
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
