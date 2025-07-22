package com.esaunders.TextbookExchange.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.mapper.BookMapper;
import com.esaunders.TextbookExchange.repository.BookListingRepository;
import com.esaunders.TextbookExchange.model.User;

import lombok.AllArgsConstructor;

/**
 * Service for book listing-related business logic.
 * Handles retrieval and validation of book listings.
 * @author Ethan Saunders
 */
@Service
@AllArgsConstructor
public class ListingService {
    /** Repository for accessing book listings. */
    private BookListingRepository bookListingRepository;

    /** Mapper for converting between BookListing and BookDto. */
    private BookMapper bookMapper;

    /**
     * Retrieves all book listings as DTOs.
     * @return a list of all BookDto objects
     */
    public List<BookDto> getAllBooks() {
        return bookListingRepository.findAll().stream()
            .map(bookMapper::toBookDto)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all book listings owned by a specific user.
     * @param user the user whose listings to retrieve
     * @return a list of BookDto objects for the user
     */
    public List<BookDto> getMyListings(User user) {
        return bookListingRepository.findByOwner(user).stream()
            .map(bookMapper::toBookDto)
            .collect(Collectors.toList());
    }

    /**
     * Checks if a course code is valid (1-3 letters followed by 3 digits).
     * @param courseCode the course code to validate
     * @return true if valid, false otherwise
     */
    public boolean checkCourseCodeValid(String courseCode) {
        return courseCode != null && courseCode.matches("^[A-Za-z]{1,3}\\d{3}$");
    }
}


