package com.esaunders.TextbookExchange.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esaunders.TextbookExchange.model.User;

/**
 * Repository interface for accessing user data in the database.
 * Extends JpaRepository for CRUD operations and custom queries.
 * @author Ethan Saunders
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /** Finds a user by their email address. */
    Optional<User> findByEmail(String email);
}
