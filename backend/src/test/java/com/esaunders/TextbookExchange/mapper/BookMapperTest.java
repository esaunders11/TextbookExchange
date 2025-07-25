package com.esaunders.TextbookExchange.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.model.BookListing;
import com.esaunders.TextbookExchange.model.User;

class BookMapperTest {

    private BookMapper bookMapper;
    private User testUser;
    private BookListing testBookListing;
    private BookDto testBookDto;

    @BeforeEach
    void setUp() {
        bookMapper = Mappers.getMapper(BookMapper.class);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("testuser");
        testUser.setEmail("test@example.com");

        // Create test book listing
        testBookListing = new BookListing();
        testBookListing.setId(1L);
        testBookListing.setTitle("Java Programming");
        testBookListing.setCourseCode("CS101");
        testBookListing.setAuthor("John Doe");
        testBookListing.setPrice(99.99);
        testBookListing.setCondition("Good");
        testBookListing.setDescription("A comprehensive Java programming book");
        testBookListing.setOwner(testUser);

        // Create test book dto
        testBookDto = new BookDto();
        testBookDto.setId(1L);
        testBookDto.setTitle("Java Programming");
        testBookDto.setCourseCode("CS101");
        testBookDto.setAuthor("John Doe");
        testBookDto.setPrice(99.99);
        testBookDto.setCondition("Good");
        testBookDto.setDescription("A comprehensive Java programming book");
        testBookDto.setOwnerId(1L);
    }

    @Test
    void testToBookDto() {
        // Test mapping from BookListing to BookDto
        BookDto result = bookMapper.toBookDto(testBookListing);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testBookListing.getId());
        assertThat(result.getTitle()).isEqualTo(testBookListing.getTitle());
        assertThat(result.getCourseCode()).isEqualTo(testBookListing.getCourseCode());
        assertThat(result.getAuthor()).isEqualTo(testBookListing.getAuthor());
        assertThat(result.getPrice()).isEqualTo(testBookListing.getPrice());
        assertThat(result.getCondition()).isEqualTo(testBookListing.getCondition());
        assertThat(result.getDescription()).isEqualTo(testBookListing.getDescription());
        assertThat(result.getOwnerId()).isEqualTo(testBookListing.getOwner().getId());
    }

    @Test
    void testToBookDtoWithNullOwner() {
        // Test mapping when owner is null
        testBookListing.setOwner(null);
        
        BookDto result = bookMapper.toBookDto(testBookListing);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testBookListing.getId());
        assertThat(result.getTitle()).isEqualTo(testBookListing.getTitle());
        assertThat(result.getOwnerId()).isNull();
    }

    @Test
    void testToBookDtoWithNullInput() {
        // Test mapping with null input
        BookDto result = bookMapper.toBookDto(null);
        assertThat(result).isNull();
    }

    @Test
    void testToEntity() {
        // Test mapping from BookDto to BookListing
        BookListing result = bookMapper.toEntity(testBookDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testBookDto.getId());
        assertThat(result.getTitle()).isEqualTo(testBookDto.getTitle());
        assertThat(result.getCourseCode()).isEqualTo(testBookDto.getCourseCode());
        assertThat(result.getAuthor()).isEqualTo(testBookDto.getAuthor());
        assertThat(result.getPrice()).isEqualTo(testBookDto.getPrice());
        assertThat(result.getCondition()).isEqualTo(testBookDto.getCondition());
        assertThat(result.getDescription()).isEqualTo(testBookDto.getDescription());
        
        // Note: owner is not mapped from DTO to entity as ownerId is not mapped back to owner object
        // This is typical behavior as the owner object needs to be resolved separately
    }

    @Test
    void testToEntityWithPartialData() {
        // Test mapping with partial data
        BookDto partialDto = new BookDto();
        partialDto.setTitle("Partial Book");
        partialDto.setPrice(50.0);

        BookListing result = bookMapper.toEntity(partialDto);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Partial Book");
        assertThat(result.getPrice()).isEqualTo(50.0);
        assertThat(result.getId()).isNull();
        assertThat(result.getCourseCode()).isNull();
        assertThat(result.getAuthor()).isNull();
        assertThat(result.getCondition()).isNull();
        assertThat(result.getDescription()).isNull();
    }

    @Test
    void testBidirectionalMapping() {
        // Test that mapping to DTO and back preserves data (except owner relationship)
        BookDto dto = bookMapper.toBookDto(testBookListing);
        BookListing entity = bookMapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(testBookListing.getId());
        assertThat(entity.getTitle()).isEqualTo(testBookListing.getTitle());
        assertThat(entity.getCourseCode()).isEqualTo(testBookListing.getCourseCode());
        assertThat(entity.getAuthor()).isEqualTo(testBookListing.getAuthor());
        assertThat(entity.getPrice()).isEqualTo(testBookListing.getPrice());
        assertThat(entity.getCondition()).isEqualTo(testBookListing.getCondition());
        assertThat(entity.getDescription()).isEqualTo(testBookListing.getDescription());
        
        // Owner is not preserved in bidirectional mapping as expected
        assertThat(entity.getOwner()).isNull();
    }
}