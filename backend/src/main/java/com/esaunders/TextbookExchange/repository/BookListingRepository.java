package com.esaunders.TextbookExchange.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esaunders.TextbookExchange.model.BookListing;
import com.esaunders.TextbookExchange.model.User;

/**
 * Repository interface for accessing book listings in the database.
 * Extends JpaRepository for CRUD operations and custom queries.
 * @author Ethan Saunders
 */
public interface BookListingRepository extends JpaRepository<BookListing, Long> {
    /** Finds book listings by course code, case-insensitive and partial match. */
    List<BookListing> findByCourseCodeContainingIgnoreCase(String courseCode);

    /** Finds book listings by title, case-insensitive and partial match. */
    List<BookListing> findByTitleContainingIgnoreCase(String title);

    /** Finds all book listings owned by a specific user. */
    List<BookListing> findByOwner(User user);

    /** Finds a book listing by its ID and owner. */
    BookListing findByIdAndOwner(Long id, User user);
}
