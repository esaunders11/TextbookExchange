package com.esaunders.TextbookExchange.dtos;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.esaunders.TextbookExchange.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails{
    private final User user;

    @Override
    public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Long getId() {
        return user.getId();
    }
}
