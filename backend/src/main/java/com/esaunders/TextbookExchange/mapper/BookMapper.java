package com.esaunders.TextbookExchange.mapper;

import org.mapstruct.Mapper;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.model.BookListing;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toBookDto(BookListing book);
    BookListing toEntity(BookDto bookDto);
} 