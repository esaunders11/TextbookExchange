package com.esaunders.TextbookExchange.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.mapper.BookMapper;
import com.esaunders.TextbookExchange.repository.BookListingRepository;
import com.esaunders.TextbookExchange.model.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ListingService {
    private BookListingRepository bookListingRepository;
    private BookMapper bookMapper;

    public List<BookDto> getAllBooks() {
        return bookListingRepository.findAll().stream()
            .map(bookMapper::toBookDto)
            .collect(Collectors.toList());
    }

    public List<BookDto> getMyListings(User user) {
        return bookListingRepository.findByOwner(user).stream()
            .map(bookMapper::toBookDto)
            .collect(Collectors.toList());
    }

    public boolean checkCourseCodeValid(String courseCode) {
        // Regex: 1-3 letters followed by 3 digits
        return courseCode != null && courseCode.matches("^[A-Za-z]{1,3}\\d{3}$");
    }
}


