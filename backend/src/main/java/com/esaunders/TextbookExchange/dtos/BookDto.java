package com.esaunders.TextbookExchange.dtos;

import lombok.Data;

@Data
public class BookDto {
    private String title;
    private String author;
    private String description;
    private double price;
    private String condition;
}
