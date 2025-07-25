package com.esaunders.TextbookExchange.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for messages in the system.
 * Represents the data sent to and from the client for messages.
 * @author Ethan Saunders
 */
@Data
@AllArgsConstructor
public class MessageDto {
    /** The unique identifier of the message. */
    private Long id;
    /** The ID of the user who sent the message. */
    private Long senderId;
    /** The name of the user who sent the message. */
    private String senderName;
    /** The ID of the user who received the message. */
    private Long receiverId;
    /** The content of the message. */
    private String content;
    /** The timestamp when the message was sent. */
    private LocalDateTime timestamp;
}
