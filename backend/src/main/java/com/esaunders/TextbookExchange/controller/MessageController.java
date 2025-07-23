package com.esaunders.TextbookExchange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import com.esaunders.TextbookExchange.model.Message;
import com.esaunders.TextbookExchange.repository.MessageRepository;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/messages")
@CrossOrigin(origins = {"https://textbook-exchange-six.vercel.app", "http://localhost:3000"}, allowCredentials = "true")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        return message;
    }



    @GetMapping("/between/{userId1}/{userId2}")
    public List<Message> getMessages(@PathVariable Long userId1, @PathVariable Long userId2) {
        List<Message> sent = messageRepository.findBySenderIdAndReceiverId(userId1, userId2);
        List<Message> received = messageRepository.findBySenderIdAndReceiverId(userId2, userId1);
        sent.addAll(received);
        sent.sort(Comparator.comparing(Message::getTimestamp));
        return sent;
    }
}