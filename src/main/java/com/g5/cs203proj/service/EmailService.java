package com.g5.cs203proj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Match;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMatchNotification(Match match) {
        Player player1 = match.getPlayer1();
        Player player2 = match.getPlayer2();

        // Send email to player1
        sendEmail(
            player1.getEmail(),
            "Chess Match Found!",
            String.format("Hello %s,\n\nYou have been matched with %s for a chess match.\n\nGood luck!",
                player1.getUsername(),
                player2.getUsername())
        );

        // Send email to player2
        sendEmail(
            player2.getEmail(),
            "Chess Match Found!",
            String.format("Hello %s,\n\nYou have been matched with %s for a chess match.\n\nGood luck!",
                player2.getUsername(),
                player1.getUsername())
        );
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
