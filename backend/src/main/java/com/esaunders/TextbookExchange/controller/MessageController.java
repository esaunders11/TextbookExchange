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

@RestController
@AllArgsConstructor
@RequestMapping("/api/messages")
@CrossOrigin(origins = {"https://textbook-exchange-six.vercel.app", "http://localhost:3000"}, allowCredentials = "true")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessagesMapper messagesMapper;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        try {
            System.out.println("Received message via WebSocket: " + message);
            
            if (message == null) {
                System.err.println("Received null message");
                return null;
            }
            
            // Validate message content
            if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                System.err.println("Message content is null or empty");
                return message; // Return as-is, don't save
            }
            
            if (message.getSenderId() == null || message.getReceiverId() == null) {
                System.err.println("SenderId or ReceiverId is null");
                return message; // Return as-is, don't save
            }
            
            message.setTimestamp(LocalDateTime.now());
            Message savedMessage = messageRepository.save(message);
            System.out.println("Message saved successfully: " + savedMessage);
            return savedMessage;
            
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
            // Return the original message even if save failed
            return message;
        }
    }

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

    @GetMapping("/between/{userId1}/{userId2}")
    public List<Message> getMessages(@PathVariable Long userId1, @PathVariable Long userId2) {
        try {
            System.out.println("Fetching messages between users: " + userId1 + " and " + userId2);
            
            List<Message> sent = messageRepository.findBySenderIdAndReceiverId(userId1, userId2);
            List<Message> received = messageRepository.findBySenderIdAndReceiverId(userId2, userId1);
            
            // Handle null results
            if (sent == null) sent = new ArrayList<>();
            if (received == null) received = new ArrayList<>();
            
            sent.addAll(received);
            sent.sort(Comparator.comparing(Message::getTimestamp));
            
            System.out.println("Found " + sent.size() + " messages between users");
            return sent;
            
        } catch (Exception e) {
            System.err.println("Error fetching messages: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list on error
        }
    }
}