package com.esaunders.TextbookExchange.dtos;

import lombok.Data;

/**
 * Data Transfer Object for login requests.
 * Contains the user's email and password for authentication.
 * @author Ethan Saunders
 */
@Data
public class LoginRequest {
    /** The user's email address. */
    private String email;

    /** The user's password. */
    private String password;
}
