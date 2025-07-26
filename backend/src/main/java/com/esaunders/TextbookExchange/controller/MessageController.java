package com.esaunders.TextbookExchange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import com.esaunders.TextbookExchange.dtos.MessageDto;
import com.esaunders.TextbookExchange.mapper.MessagesMapper;
import com.esaunders.TextbookExchange.model.Message;
import com.esaunders.TextbookExchange.repository.MessageRepository;
import com.esaunders.TextbookExchange.repository.UserRepository;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Controller for handling chat messages and message retrieval endpoints.
 * Supports WebSocket messaging and REST API for message history.
 * @author Ethan Saunders
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/messages")
@CrossOrigin(origins = {"https://textbook-exchange-six.vercel.app", "http://localhost:3000"}, allowCredentials = "true")
public class MessageController {

    /** Repository for message entities. */
    @Autowired
    private MessageRepository messageRepository;

    /** Repository for user entities. */
    @Autowired
    private UserRepository userRepository;

    /** Mapper for converting Message to MessageDto. */
    @Autowired
    private MessagesMapper messagesMapper;

    /**
     * Handles incoming chat messages via WebSocket and saves them to the database.
     * @param message the incoming message
     * @return the saved message
     */
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MessageDto send(Message message) {
        try {
            System.out.println("Received message via WebSocket: " + message);
            
            if (message == null) {
                System.err.println("Received null message");
                return null;
            }
            
            // Validate message content
            if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                System.err.println("Message content is null or empty");
                return messagesMapper.toDto(message, userRepository);
            }
            
            if (message.getSenderId() == null || message.getReceiverId() == null) {
                System.err.println("SenderId or ReceiverId is null");
                return messagesMapper.toDto(message, userRepository);
            }
            
            message.setTimestamp(LocalDateTime.now());
            Message savedMessage = messageRepository.save(message);
            MessageDto savedMessageDto = messagesMapper.toDto(savedMessage, userRepository);
            System.out.println("Message saved successfully: " + savedMessage);
            return savedMessageDto;
            
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
            return messagesMapper.toDto(message, userRepository);
        }
    }

    /**
     * Gets all messages received by a user.
     * @param userId the receiver's user ID
     * @return list of received MessageDto objects
     */
    @GetMapping("/received/{userId}")
    public ResponseEntity<List<MessageDto>> getReceivedMessages(@PathVariable Long userId) {
        try {
            System.out.println("Fetching received messages for user: " + userId);
            List<MessageDto> messages = messageRepository
                                        .findByReceiverId(userId)
                                        .stream()
                                        .map(message -> messagesMapper.toDto(message, userRepository))
                                        .collect(Collectors.toList());

            if (messages == null) {
                messages = new ArrayList<>();
            }
            
            messages.sort(Comparator.comparing(MessageDto::getTimestamp));
            System.out.println("Found " + messages.size() + " received messages for user");
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            System.err.println("Error fetching received messages: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    /**
     * Gets all messages sent and received between two users.
     * @param userId1 the first user's ID
     * @param userId2 the second user's ID
     * @return list of MessageDto objects between the users
     */
    @GetMapping("/between/{userId1}/{userId2}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long userId1, @PathVariable Long userId2) {
        try {
            System.out.println("Fetching messages between users: " + userId1 + " and " + userId2);
            
            List<Message> sent = messageRepository.findBySenderIdAndReceiverId(userId1, userId2);
            List<Message> received = messageRepository.findBySenderIdAndReceiverId(userId2, userId1);
            
            if (sent == null) sent = new ArrayList<>();
            if (received == null) received = new ArrayList<>();
            
            sent.addAll(received);
            sent.sort(Comparator.comparing(Message::getTimestamp));
            
            System.out.println("Found " + sent.size() + " messages between users");
            return sent
                .stream()
                .map(message -> messagesMapper.toDto(message, userRepository))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
            
        } catch (Exception e) {
            System.err.println("Error fetching messages: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
}