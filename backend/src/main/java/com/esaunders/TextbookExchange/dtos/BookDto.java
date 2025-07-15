package com.esaunders.TextbookExchange.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String courseCode;
    private String isbn;
    private String description;
    private double price;
    private String condition;
    private Long ownerId;
    private LocalDateTime postedAt;
}
