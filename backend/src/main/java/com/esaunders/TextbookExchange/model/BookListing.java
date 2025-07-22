package com.esaunders.TextbookExchange.model;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a book listing in the system.
 * Maps to the book_listings table in the database.
 * @author Ethan Saunders
 */
@Entity
@Table(name = "book_listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookListing {
    /** The unique identifier of the book listing. */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The title of the book. */
    private String title;

    /** The author of the book. */
    private String author;

    /** The ISBN of the book. */
    private String isbn;

    /** The course code associated with the book. */
    @Column(name = "course_code")
    private String courseCode;

    /** The price of the book. */
    private double price;

    /** The condition of the book. */
    private String condition;

    /** The description of the book. */
    private String description;

    /** The user who owns this book listing. */
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /** The date and time when the listing was posted. */
    @Column(name = "posted_at")
    private LocalDateTime postedAt;
}
