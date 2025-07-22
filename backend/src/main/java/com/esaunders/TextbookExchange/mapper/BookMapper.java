package com.esaunders.TextbookExchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.model.BookListing;

/**
 * Mapper interface for converting between BookListing entities and BookDto objects.
 * Uses MapStruct for automatic mapping.
 * @author Ethan Saunders
 */
@Mapper(componentModel = "spring")
public interface BookMapper {
    /**
     * Maps a BookListing entity to a BookDto.
     * Sets the ownerId field in the DTO from the owner's id in the entity.
     *
     * @param book the BookListing entity
     * @return the mapped BookDto
     */
    @Mapping(target = "ownerId", source = "owner.id")
    BookDto toBookDto(BookListing book);

    /**
     * Maps a BookDto to a BookListing entity.
     *
     * @param bookDto the BookDto
     * @return the mapped BookListing entity
     */
    BookListing toEntity(BookDto bookDto);
}