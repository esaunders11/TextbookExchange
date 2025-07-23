package com.esaunders.TextbookExchange.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entity representing a message in the system.
 * Maps to the messages table in the database.
 * @author Ethan Saunders
 */
@Entity
@Table(name = "messages")
@Data
@AllArgsConstructor
public class Message {
    /** The unique identifier of the message. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** The ID of the user who sent the message. */
    private Long senderId;
    /** The ID of the user who received the message. */
    private Long receiverId;
    /** The content of the message. */
    private String content;
    /** The date and time when the message was sent. */
    private LocalDateTime timestamp;

}
