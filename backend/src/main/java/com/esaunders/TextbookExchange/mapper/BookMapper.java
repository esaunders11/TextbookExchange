package com.esaunders.TextbookExchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.esaunders.TextbookExchange.dtos.BookDto;
import com.esaunders.TextbookExchange.model.BookListing;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "ownerId", source = "owner.id")
    BookDto toBookDto(BookListing book);
    BookListing toEntity(BookDto bookDto);
}