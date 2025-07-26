package com.esaunders.TextbookExchange.model;

import jakarta.persistence.Id;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user in the system.
 * Maps to the users table in the database.
 * @author Ethan Saunders
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /** The unique identifier of the user. */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user's first name. */
    private String firstName;

    /** The user's last name. */
    private String lastName;

    /** The user's username (must be unique and not null). */
    @Column(unique = true, nullable = false)
    private String username;

    /** The user's email address (must be unique and not null). */
    @Column(unique = true, nullable = false)
    private String email;

    /** The user's password (not null). */
    @Column(nullable = false)
    private String password;

    /** Whether the user is verified. */
    private boolean verified;

    /** The date the user was created. */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
