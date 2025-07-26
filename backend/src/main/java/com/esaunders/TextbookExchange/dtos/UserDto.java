package com.esaunders.TextbookExchange.dtos;

import lombok.Data;

/**
 * Data Transfer Object for user information.
 * Represents user data sent to and from the client.
 * @author Ethan Saunders
 */
@Data
public class UserDto {
    /** The unique identifier of the user. */
    private Long id;

    /** The user's first name. */
    private String firstName;

    /** The user's last name. */
    private String lastName;

    /** The user's username (must be unique and not null). */
    private String username;

    /** The user's email address. */
    private String email;
}
