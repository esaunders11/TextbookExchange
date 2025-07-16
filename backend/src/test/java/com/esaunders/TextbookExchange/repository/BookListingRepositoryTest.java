package com.esaunders.TextbookExchange.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.esaunders.TextbookExchange.model.BookListing;
import com.esaunders.TextbookExchange.model.User;

@DataJpaTest
class BookListingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookListingRepository bookListingRepository;

    private User testUser1;
    private User testUser2;
    private BookListing bookListing1;
    private BookListing bookListing2;
    private BookListing bookListing3;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User();
        testUser1.setFirstName("testuser1");
        testUser1.setEmail("test1@example.com");
        testUser1 = entityManager.persistAndFlush(testUser1);

        testUser2 = new User();
        testUser2.setFirstName("testuser2");
        testUser2.setEmail("test2@example.com");
        testUser2 = entityManager.persistAndFlush(testUser2);

        // Create test book listings
        bookListing1 = new BookListing();
        bookListing1.setTitle("Java Programming");
        bookListing1.setCourseCode("CS101");
        bookListing1.setOwner(testUser1);
        bookListing1 = entityManager.persistAndFlush(bookListing1);

        bookListing2 = new BookListing();
        bookListing2.setTitle("Advanced Java Concepts");
        bookListing2.setCourseCode("CS201");
        bookListing2.setOwner(testUser1);
        bookListing2 = entityManager.persistAndFlush(bookListing2);

        bookListing3 = new BookListing();
        bookListing3.setTitle("Python Programming");
        bookListing3.setCourseCode("CS102");
        bookListing3.setOwner(testUser2);
        bookListing3 = entityManager.persistAndFlush(bookListing3);
    }

    @Test
    void testFindByCourseCodeContainingIgnoreCase() {
        // Test exact match
        List<BookListing> result = bookListingRepository.findByCourseCodeContainingIgnoreCase("CS101");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCourseCode()).isEqualTo("CS101");

        // Test case insensitive
        result = bookListingRepository.findByCourseCodeContainingIgnoreCase("cs101");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCourseCode()).isEqualTo("CS101");

        // Test partial match
        result = bookListingRepository.findByCourseCodeContainingIgnoreCase("CS1");
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BookListing::getCourseCode)
                .containsExactlyInAnyOrder("CS101", "CS102");

        // Test no match
        result = bookListingRepository.findByCourseCodeContainingIgnoreCase("MATH");
        assertThat(result).isEmpty();
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        // Test exact match
        List<BookListing> result = bookListingRepository.findByTitleContainingIgnoreCase("Java Programming");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Java Programming");

        // Test case insensitive
        result = bookListingRepository.findByTitleContainingIgnoreCase("java programming");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Java Programming");

        // Test partial match
        result = bookListingRepository.findByTitleContainingIgnoreCase("Java");
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BookListing::getTitle)
                .containsExactlyInAnyOrder("Java Programming", "Advanced Java Concepts");

        // Test partial match with different case
        result = bookListingRepository.findByTitleContainingIgnoreCase("PROGRAMMING");
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BookListing::getTitle)
                .containsExactlyInAnyOrder("Java Programming", "Python Programming");

        // Test no match
        result = bookListingRepository.findByTitleContainingIgnoreCase("Chemistry");
        assertThat(result).isEmpty();
    }

    @Test
    void testFindByOwner() {
        // Test finding books by testUser1
        List<BookListing> result = bookListingRepository.findByOwner(testUser1);
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BookListing::getTitle)
                .containsExactlyInAnyOrder("Java Programming", "Advanced Java Concepts");

        // Test finding books by testUser2
        result = bookListingRepository.findByOwner(testUser2);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Python Programming");

        // Test with user who has no books
        User userWithNoBooks = new User();
        userWithNoBooks.setFirstName("nobooksuser");
        userWithNoBooks.setEmail("nobooks@example.com");
        userWithNoBooks = entityManager.persistAndFlush(userWithNoBooks);

        result = bookListingRepository.findByOwner(userWithNoBooks);
        assertThat(result).isEmpty();
    }

    @Test
    void testFindByIdAndOwner() {
        // Test finding existing book with correct owner
        BookListing result = bookListingRepository.findByIdAndOwner(bookListing1.getId(), testUser1);
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Java Programming");
        assertThat(result.getOwner()).isEqualTo(testUser1);

        // Test finding existing book with wrong owner
        result = bookListingRepository.findByIdAndOwner(bookListing1.getId(), testUser2);
        assertThat(result).isNull();

        // Test finding non-existing book
        result = bookListingRepository.findByIdAndOwner(999L, testUser1);
        assertThat(result).isNull();

        // Test with null owner
        result = bookListingRepository.findByIdAndOwner(bookListing1.getId(), null);
        assertThat(result).isNull();
    }

    @Test
    void testJpaRepositoryMethods() {
        // Test count
        long count = bookListingRepository.count();
        assertThat(count).isEqualTo(3);

        // Test findById
        Optional<BookListing> result = bookListingRepository.findById(bookListing1.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Java Programming");

        // Test findAll
        List<BookListing> allBooks = bookListingRepository.findAll();
        assertThat(allBooks).hasSize(3);

        // Test delete
        bookListingRepository.delete(bookListing1);
        assertThat(bookListingRepository.count()).isEqualTo(2);
        assertThat(bookListingRepository.findById(bookListing1.getId())).isEmpty();
    }
}