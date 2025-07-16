package com.esaunders.TextbookExchange.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.esaunders.TextbookExchange.dtos.CustomUserDetails;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User user;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        customUserDetails = new CustomUserDetails(user);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAuthenticatedUser_ShouldReturnUserWhenAuthenticated() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getAuthenticatedUser();

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
        assertEquals(1L, result.getId());
        assertEquals("test@test.com", result.getEmail());
        
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
        verify(userRepository).findById(1L);
    }

    @Test
    void getAuthenticatedUser_ShouldThrowExceptionWhenAuthenticationIsNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getAuthenticatedUser());
        
        assertEquals("Unauthenticated", exception.getMessage());
        verify(securityContext).getAuthentication();
        verifyNoInteractions(userRepository);
    }

    @Test
    void getAuthenticatedUser_ShouldThrowExceptionWhenNotAuthenticated() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getAuthenticatedUser());
        
        assertEquals("Unauthenticated", exception.getMessage());
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verifyNoInteractions(userRepository);
    }

    @Test
    void getAuthenticatedUser_ShouldThrowExceptionWhenPrincipalIsNotCustomUserDetails() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("not a CustomUserDetails object");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getAuthenticatedUser());
        
        assertEquals("Invalid user", exception.getMessage());
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
        verifyNoInteractions(userRepository);
    }

    @Test
    void getAuthenticatedUser_ShouldThrowExceptionWhenUserNotFoundInDatabase() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getAuthenticatedUser());
        
        assertEquals("User not found", exception.getMessage());
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
        verify(userRepository).findById(1L);
    }
}
