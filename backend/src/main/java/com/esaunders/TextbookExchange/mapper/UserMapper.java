package com.esaunders.TextbookExchange.mapper;

import org.mapstruct.Mapper;

import com.esaunders.TextbookExchange.dtos.RegisterUser;
import com.esaunders.TextbookExchange.dtos.UserDto;
import com.esaunders.TextbookExchange.model.User;

/**
 * Mapper interface for converting between User entities and DTOs.
 * Uses MapStruct for automatic mapping.
 * @author Ethan Saunders
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Maps a User entity to a UserDto.
     *
     * @param user the User entity
     * @return the mapped UserDto
     */
    UserDto toUserDto(User user);

    /**
     * Maps a RegisterUser DTO to a User entity.
     *
     * @param userDto the RegisterUser DTO
     * @return the mapped User entity
     */
    User toEntity(RegisterUser userDto);

    /**
     * Maps a UserDto to a User entity.
     *
     * @param userDto the UserDto
     * @return the mapped User entity
     */
    User toEntity(UserDto userDto);
}
