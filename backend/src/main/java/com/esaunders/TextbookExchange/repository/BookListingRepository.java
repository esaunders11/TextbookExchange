package com.esaunders.TextbookExchange.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esaunders.TextbookExchange.model.BookListing;

public interface BookListingRepository extends JpaRepository<BookListing, Long> {
    List<BookListing> findByCourseCodeContainingIgnoreCase(String courseCode);
    List<BookListing> findByTitleContainingIgnoreCase(String title);
}
