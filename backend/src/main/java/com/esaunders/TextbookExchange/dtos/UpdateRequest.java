package com.esaunders.TextbookExchange.dtos;

import lombok.Data;

/**
 * Data Transfer Object for updating user profile information.
 * Contains fields for updating the user's first and last name.
 * @author Ethan Saunders
 */
@Data
public class UpdateRequest {
    /** The user's new first name. */
    private String firstName;

    /** The user's new last name. */
    private String lastName;
}
