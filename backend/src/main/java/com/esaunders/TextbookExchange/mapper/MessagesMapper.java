package com.esaunders.TextbookExchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Context;

import com.esaunders.TextbookExchange.dtos.MessageDto;
import com.esaunders.TextbookExchange.model.Message;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.UserRepository;

/**
 * Mapper for converting Message entities to MessageDto and vice versa.
 * Uses UserRepository to resolve sender names.
 * @author Ethan Saunders
 */
@Mapper(componentModel = "spring")
public interface MessagesMapper {
    /**
     * Maps a Message entity to a MessageDto, resolving sender name from senderId.
     * @param message the message entity
     * @param userRepository the user repository for sender lookup
     * @return the mapped MessageDto
     */
    @Mapping(target = "senderName", expression = "java(getSenderName(message.getSenderId(), userRepository))")
    MessageDto toDto(Message message, @Context UserRepository userRepository);

    /**
     * Maps a MessageDto to a Message entity.
     * @param messageDto the message DTO
     * @return the mapped Message entity
     */
    Message toEntity(MessageDto messageDto);

    /**
     * Gets the sender's full name from the user repository.
     * @param senderId the sender's user ID
     * @param userRepository the user repository
     * @return the sender's full name or "Unknown" if not found
     */
    default String getSenderName(Long senderId, UserRepository userRepository) {
        User user = userRepository.findById(senderId).orElse(null);
        return user != null ? user.getUsername() : "Unknown";
    }
}
