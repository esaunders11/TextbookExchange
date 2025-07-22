package com.esaunders.TextbookExchange.dtos;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Data Transfer Object for book listings.
 * Represents the data sent to and from the client for book listings.
 * @author Ethan Saunders
 */
@Data
public class BookDto {
    /** The unique identifier of the book listing. */
    private Long id;

    /** The title of the book. */
    private String title;

    /** The author of the book. */
    private String author;

    /** The course code associated with the book. */
    private String courseCode;

    /** The ISBN of the book. */
    private String isbn;

    /** The description of the book. */
    private String description;

    /** The price of the book. */
    private double price;

    /** The condition of the book (e.g., NEW, GOOD, FAIR, POOR). */
    private String condition;

    /** The ID of the user who owns the listing. */
    private Long ownerId;

    /** The date and time when the listing was posted. */
    private LocalDateTime postedAt;
}
