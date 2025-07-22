package com.esaunders.TextbookExchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esaunders.TextbookExchange.model.VerificationToken;

/**
 * Repository interface for accessing verification tokens in the database.
 * Extends JpaRepository for CRUD operations and custom queries.
 * @author Ethan Saunders
 */
public interface VerficationTokenRepository extends JpaRepository<VerificationToken, Long> {
    /**
     * Finds a verification token by its token string.
     * @param token the token string to search for
     * @return the VerificationToken if found, otherwise null
     */
    VerificationToken findByToken(String token);
    
}
