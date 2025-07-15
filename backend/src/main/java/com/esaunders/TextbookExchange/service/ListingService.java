package com.esaunders.TextbookExchange.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.mapper.BookMapper;
import com.esaunders.TextbookExchange.repository.BookListingRepository;

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
}


