package com.esaunders.TextbookExchange.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a verification token for user registration.
 * Used to verify user email addresses during the registration process.
 * @author Ethan Saunders
 */
@Entity
@Table(name = "verification_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {
    /** The unique identifier of the verification token. */
    @Id
    private String token;
    /** The user associated with this verification token. */
    @OneToOne
    private User user;
    /** The date and time when the token will expire. */
    private LocalDateTime expiryTime;

    /**
     * Checks if the verification token is expired.
     * @return true if the current time is after the expiry time, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

}
