package com.esaunders.TextbookExchange.dtos;

import lombok.Data;

@Data
public class RegisterUser {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean verified = true;
}
