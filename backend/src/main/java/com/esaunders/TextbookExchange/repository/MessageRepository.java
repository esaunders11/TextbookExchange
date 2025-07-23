package com.esaunders.TextbookExchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esaunders.TextbookExchange.model.Message;

import java.util.List;

/**
 * Repository interface for accessing messages in the database.
 * Extends JpaRepository for CRUD operations and custom queries.
 * @author Ethan Saunders
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    /**
     * Finds messages sent by a specific sender to a specific receiver.
     * @param senderId the ID of the sender
     * @param receiverId the ID of the receiver
     * @return a list of messages sent by the sender to the receiver
     */
    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    /**
     * Finds messages received by a specific receiver from a specific sender.
     * @param receiverId the ID of the receiver
     * @param senderId the ID of the sender
     * @return a list of messages received by the receiver from the sender
     */
    List<Message> findByReceiverIdAndSenderId(Long receiverId, Long senderId);
}