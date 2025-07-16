package com.esaunders.TextbookExchange.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.mapper.BookMapper;
import com.esaunders.TextbookExchange.model.BookListing;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.BookListingRepository;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private BookListingRepository bookListingRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private ListingService listingService;

    private BookListing bookListing1;
    private BookListing bookListing2;
    private BookDto bookDto1;
    private BookDto bookDto2;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        bookListing1 = new BookListing();
        bookListing1.setId(1L);
        bookListing1.setTitle("Java Programming");
        bookListing1.setOwner(user);

        bookListing2 = new BookListing();
        bookListing2.setId(2L);
        bookListing2.setTitle("Spring Boot Guide");
        bookListing2.setOwner(user);

        bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setTitle("Java Programming");

        bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setTitle("Spring Boot Guide");
    }

    @Test
    void getAllBooks_ShouldReturnAllBooksAsDtos() {
        // Arrange
        List<BookListing> bookListings = Arrays.asList(bookListing1, bookListing2);
        when(bookListingRepository.findAll()).thenReturn(bookListings);
        when(bookMapper.toBookDto(bookListing1)).thenReturn(bookDto1);
        when(bookMapper.toBookDto(bookListing2)).thenReturn(bookDto2);

        // Act
        List<BookDto> result = listingService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookDto1, result.get(0));
        assertEquals(bookDto2, result.get(1));
        
        verify(bookListingRepository).findAll();
        verify(bookMapper).toBookDto(bookListing1);
        verify(bookMapper).toBookDto(bookListing2);
    }

    @Test
    void getAllBooks_ShouldReturnEmptyListWhenNoBooks() {
        // Arrange
        when(bookListingRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<BookDto> result = listingService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookListingRepository).findAll();
        verifyNoInteractions(bookMapper);
    }

    @Test
    void getMyListings_ShouldReturnUserBooksAsDtos() {
        // Arrange
        List<BookListing> userListings = Arrays.asList(bookListing1, bookListing2);
        when(bookListingRepository.findByOwner(user)).thenReturn(userListings);
        when(bookMapper.toBookDto(bookListing1)).thenReturn(bookDto1);
        when(bookMapper.toBookDto(bookListing2)).thenReturn(bookDto2);

        // Act
        List<BookDto> result = listingService.getMyListings(user);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookDto1, result.get(0));
        assertEquals(bookDto2, result.get(1));
        
        verify(bookListingRepository).findByOwner(user);
        verify(bookMapper).toBookDto(bookListing1);
        verify(bookMapper).toBookDto(bookListing2);
    }

    @Test
    void getMyListings_ShouldReturnEmptyListWhenUserHasNoListings() {
        // Arrange
        when(bookListingRepository.findByOwner(user)).thenReturn(Arrays.asList());

        // Act
        List<BookDto> result = listingService.getMyListings(user);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookListingRepository).findByOwner(user);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void checkCourseCodeValid_ShouldReturnTrueForValidCodes() {
        // Test valid patterns
        assertTrue(listingService.checkCourseCodeValid("CS101"));
        assertTrue(listingService.checkCourseCodeValid("MAT201"));
        assertTrue(listingService.checkCourseCodeValid("PHY300"));
        assertTrue(listingService.checkCourseCodeValid("A123"));
        assertTrue(listingService.checkCourseCodeValid("ABC456"));
        assertTrue(listingService.checkCourseCodeValid("cs101")); // lowercase
        assertTrue(listingService.checkCourseCodeValid("CsE123")); // mixed case
    }

    @Test
    void checkCourseCodeValid_ShouldReturnFalseForInvalidCodes() {
        // Test invalid patterns
        assertFalse(listingService.checkCourseCodeValid(null));
        assertFalse(listingService.checkCourseCodeValid(""));
        assertFalse(listingService.checkCourseCodeValid("CS12")); // only 2 digits
        assertFalse(listingService.checkCourseCodeValid("CS1234")); // 4 digits
        assertFalse(listingService.checkCourseCodeValid("ABCD123")); // 4 letters
        assertFalse(listingService.checkCourseCodeValid("123")); // no letters
        assertFalse(listingService.checkCourseCodeValid("CS")); // no digits
        assertFalse(listingService.checkCourseCodeValid("CS-101")); // special character
        assertFalse(listingService.checkCourseCodeValid("CS 101")); // space
        assertFalse(listingService.checkCourseCodeValid("1CS101")); // digit first
    }
}
