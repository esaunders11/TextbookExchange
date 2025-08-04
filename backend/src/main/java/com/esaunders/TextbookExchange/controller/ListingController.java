package com.esaunders.TextbookExchange.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.dtos.BookRequest;
import com.esaunders.TextbookExchange.mapper.BookMapper;
import com.esaunders.TextbookExchange.mapper.UserMapper;
import com.esaunders.TextbookExchange.model.BookListing;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.BookListingRepository;
import com.esaunders.TextbookExchange.service.ListingService;
import com.esaunders.TextbookExchange.service.S3Service;
import com.esaunders.TextbookExchange.service.UserService;

import lombok.AllArgsConstructor;

/**
 * Controller for book listing-related endpoints.
 * Handles listing retrieval, search, posting, and deletion.
 * @author Ethan Saunders
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/books")
@CrossOrigin(origins = {"https://textbook-exchange-six.vercel.app", "http://localhost:3000", "http://localhost:4000"}, allowCredentials = "true")
public class ListingController {

    /** Repository for accessing book listings. */
    private BookListingRepository bookListingRepository;

    /** Mapper for converting between BookListing and BookDto.*/
    private BookMapper bookMapper;

    /** Service for listing-related business logic. */
    private UserMapper userMapper;

    /** Service for listing-related business logic. */
    private ListingService listingService;

    /** Service for user-related operations. */
    private UserService userService;

    /** Service for S3 operations. */
    private S3Service s3Service;

    /**
     * Returns the 10 most recent book listings, excluding the current user's own listings.
     *
     * @return a response entity with a list of BookDto
     */
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        User user = userService.getAuthenticatedUser();

        List<BookDto> books = bookListingRepository.findAll().stream()
            .map(book -> {
                BookDto dto = bookMapper.toBookDto(book);
                if (book.getOwner() != null) {
                    dto.setSeller(userMapper.toUserDto(book.getOwner()));
                }
                return dto;
            })
            .filter(book -> user == null || book.getOwnerId() == null || !book.getOwnerId().equals(user.getId()))
            .sorted((a, b) -> {
                if (a.getPostedAt() == null && b.getPostedAt() == null) return 0;
                if (a.getPostedAt() == null) return 1;
                if (b.getPostedAt() == null) return -1;
                return b.getPostedAt().compareTo(a.getPostedAt());
            })
            .limit(10)
            .collect(Collectors.toList());

        return ResponseEntity.ok(books);
    }

    /**
     * Searches book listings by query, condition, and price range.
     * Excludes the current user's own listings.
     *
     * @param query search term for title or course code (optional)
     * @param condition book condition filter (optional)
     * @param minPrice minimum price filter (optional)
     * @param maxPrice maximum price filter (optional)
     * @return a response entity with a list of BookDto matching the filters
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String condition,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice
    ) {
        User user = userService.getAuthenticatedUser();
        List<BookDto> books = bookListingRepository.findAll().stream()
            .map(book -> {
                BookDto dto = bookMapper.toBookDto(book);
                if (book.getOwner() != null) {
                    dto.setSeller(userMapper.toUserDto(book.getOwner()));
                }
                return dto;
            })
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

    /**
     * Posts a new book listing for the authenticated user.
     *
     * @param bookDto the book listing data
     * @return a response entity indicating success or error
     */
    @PostMapping(path = "/post-listing", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> postBookListing(
        @RequestPart("request") BookRequest request,
        @RequestPart("image") MultipartFile image
    ) {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body("Book image is required.");
        }
        // Validate image type
        String contentType = image.getContentType();
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            return ResponseEntity.badRequest().body("Invalid image type. Only JPEG and PNG are allowed.");
        }

        BookListing bookListing = bookMapper.toEntity(request);
        if (!listingService.checkCourseCodeValid(bookListing.getCourseCode())) {
            return ResponseEntity.badRequest().body("Invalid course code format");
        }
        bookListing.setOwner(user);
        bookListing.setPostedAt(LocalDateTime.now());

        try {
            String ext = "jpg";
            if ("image/png".equals(contentType)) ext = "png";
            String key = "book-images/" + System.currentTimeMillis() + "-" + user.getId() + "." + ext;
            String imageUrl = s3Service.uploadFile(image, key);
            bookListing.setImageUrl(imageUrl);
        } catch (Exception e) {
            System.out.println("Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
        }

        bookListingRepository.save(bookListing);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a book listing owned by the authenticated user.
     *
     * @param id the ID of the book listing to delete
     * @return a response entity indicating success or error
     */
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

    /**
     * Gets a book listing by its ID.
     *
     * @param id the ID of the book listing
     * @return a response entity with the BookDto or not found status
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        BookListing bookListing = bookListingRepository.findById(id).orElse(null);
        if (bookListing == null) {
            return ResponseEntity.notFound().build();
        }

        BookDto bookDto = bookMapper.toBookDto(bookListing);
        if (bookListing.getOwner() != null) {
            bookDto.setSeller(userMapper.toUserDto(bookListing.getOwner()));
        }
        return ResponseEntity.ok(bookDto);
    }

    /**
     * Returns all book listings owned by the authenticated user.
     *
     * @return a response entity with a list of BookDto for the user's listings
     */
    @GetMapping("/my-listings")
    public ResponseEntity<List<BookDto>> getMyListings() {
        User user  = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(listingService.getMyListings(user));
    }

}
