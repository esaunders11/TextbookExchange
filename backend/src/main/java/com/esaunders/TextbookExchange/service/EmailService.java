package com.esaunders.TextbookExchange.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.esaunders.TextbookExchange.model.User;
import com.esaunders.TextbookExchange.model.VerificationToken;
import com.esaunders.TextbookExchange.repository.UserRepository;
import com.esaunders.TextbookExchange.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Deletes users who are not verified and were created more than 24 hours ago.
     * Runs every day at 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void deleteUnverifiedUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.isVerified() && user.getCreatedAt() != null && user.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
                userRepository.delete(user);
            }
        }
    }

    /**
     * Deletes expired verification tokens.
     * Runs every hour.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredTokens() {
        List<VerificationToken> tokens = verificationTokenRepository.findAll();
        for (VerificationToken token : tokens) {
            if (token.getExpiryTime() != null && token.getExpiryTime().isBefore(LocalDateTime.now())) {
                verificationTokenRepository.delete(token);
            }
        }
    }

    
}
