package com.esaunders.TextbookExchange.mapper;

import org.mapstruct.Mapper;

import com.esaunders.TextbookExchange.dtos.RegisterUser;
import com.esaunders.TextbookExchange.dtos.UserDto;
import com.esaunders.TextbookExchange.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);
    User toEntity(RegisterUser userDto);
    User toEntity(UserDto userDto);
}
