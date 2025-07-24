package com.esaunders.TextbookExchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Context;

import com.esaunders.TextbookExchange.dtos.MessageDto;
import com.esaunders.TextbookExchange.model.Message;
import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.repository.UserRepository;

@Mapper(componentModel = "spring")
public interface MessagesMapper {
    @Mapping(target = "senderName", expression = "java(getSenderName(message.getSenderId(), userRepository))")
    MessageDto toDto(Message message, @Context UserRepository userRepository);
    Message toEntity(MessageDto messageDto);

    default String getSenderName(Long senderId, UserRepository userRepository) {
        User user = userRepository.findById(senderId).orElse(null);
        return user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown";
    }
}
