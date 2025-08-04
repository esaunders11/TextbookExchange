package com.esaunders.TextbookExchange.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esaunders.TextbookExchange.dtos.UserDto;
import com.esaunders.TextbookExchange.mapper.UserMapper;
import com.esaunders.TextbookExchange.repository.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin(origins = {"https://textbook-exchange-six.vercel.app", "http://localhost:3000", "http://localhost:4000"}, allowCredentials = "true")
/**
 * Controller for user-related endpoints.
 * Currently, this controller does not have any specific endpoints defined.
 * @author Ethan Saunders
 */
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        var user = userRepository.getUserById(id);
        return userMapper.toUserDto(user);
    }
}