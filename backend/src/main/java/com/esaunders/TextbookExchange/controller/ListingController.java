package com.esaunders.TextbookExchange.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.repository.BookListingRepository;
import com.esaunders.TextbookExchange.service.ListingService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ListingController {
    private BookListingRepository bookListingRepository;
    private ListingService listingService;
    
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(listingService.getAllBooks());
    }

}
