package com.esaunders.TextbookExchange.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.mapper.BookMapper;
import com.esaunders.TextbookExchange.model.BookListing;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.BookListingRepository;
import com.esaunders.TextbookExchange.service.ListingService;
import com.esaunders.TextbookExchange.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ListingController {
    private BookListingRepository bookListingRepository;
    private BookMapper bookMapper;
    private ListingService listingService;
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        User user = userService.getAuthenticatedUser();

        List<BookDto> books = bookListingRepository.findAll().stream()
            .map(bookMapper::toBookDto)
            .filter(book -> user == null || book.getOwnerId() == null || !book.getOwnerId().equals(user.getId()))
            .sorted((a, b) -> {
                // Sort by postedAt descending (most recent first)
                if (a.getPostedAt() == null && b.getPostedAt() == null) return 0;
                if (a.getPostedAt() == null) return 1;
                if (b.getPostedAt() == null) return -1;
                return b.getPostedAt().compareTo(a.getPostedAt());
            })
            .limit(10)
            .collect(Collectors.toList());

        return ResponseEntity.ok(books);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String condition,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice
    ) {
        User user = userService.getAuthenticatedUser();
        List<BookDto> books = bookListingRepository.findAll().stream()
            .map(bookMapper::toBookDto)
            .filter(book -> {
                boolean matches = true;
                if (user != null && book.getOwnerId() != null && book.getOwnerId().equals(user.getId())) {
                    return false;
                }
                if (query != null && !query.isEmpty()) {
                    String q = query.toLowerCase();
                    matches &= (book.getTitle() != null && book.getTitle().toLowerCase().contains(q))
                            || (book.getCourseCode() != null && book.getCourseCode().toLowerCase().contains(q));
                }
                if (condition != null && !condition.isEmpty()) {
                    matches &= condition.equalsIgnoreCase(book.getCondition());
                }
                if (minPrice != null) {
                    matches &= book.getPrice() >= minPrice;
                }
                if (maxPrice != null) {
                    matches &= book.getPrice() <= maxPrice;
                }
                return matches;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(books);
    }

    @PostMapping("/post-listing")
    public ResponseEntity<?> postBookListing(@RequestBody BookDto bookDto) {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        BookListing bookListing = bookMapper.toEntity(bookDto);
        if (!listingService.checkCourseCodeValid(bookListing.getCourseCode())) {
            return ResponseEntity.badRequest().body("Invalid course code format");
        }
        bookListing.setOwner(user);
        bookListing.setPostedAt(LocalDateTime.now());
        bookListingRepository.save(bookListing);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookListing(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        BookListing bookListing = bookListingRepository.findByIdAndOwner(id, user);
        if (bookListing == null) {
            new RuntimeException("Book listing not found");
        }

        if (!bookListing.getOwner().equals(user)) {
            return ResponseEntity.status(403).build();
        }
        
        bookListingRepository.delete(bookListing);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-listings")
    public ResponseEntity<List<BookDto>> getMyListings() {
        User user  = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(listingService.getMyListings(user));
    }

}
