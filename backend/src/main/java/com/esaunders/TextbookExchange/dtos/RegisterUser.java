package com.esaunders.TextbookExchange.dtos;

import lombok.Data;

/**
 * Data Transfer Object for user registration requests.
 * Contains the user's registration details.
 * @author Ethan Saunders
 */
@Data
public class RegisterUser {
    /** The user's first name. */
    private String firstName;

    /** The user's last name. */
    private String lastName;

    /** The user's username (must be unique and not null). */
    private String username;

    /** The user's email address. */
    private String email;

    /** The user's password. */
    private String password;

    /** Whether the user is verified (default false). */
    private boolean verified = false;
}
