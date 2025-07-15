package com.esaunders.TextbookExchange.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esaunders.TextbookExchange.model.BookListing;
import com.esaunders.TextbookExchange.model.User;

public interface BookListingRepository extends JpaRepository<BookListing, Long> {
    List<BookListing> findByCourseCodeContainingIgnoreCase(String courseCode);
    List<BookListing> findByTitleContainingIgnoreCase(String title);
    List<BookListing> findByOwner(com.esaunders.TextbookExchange.model.User user);
    BookListing findByIdAndOwner(Long id, User user);
}
