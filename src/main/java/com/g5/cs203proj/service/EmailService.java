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
            "Knight to Meet You - Your Chess Match is Ready!",
            String.format(
                "Good day you stunning stack of sunshine %s,\n\n" +
                "You have been matched for a chess match.\n" +
                "It's game time and get ready to slay your tournament with %s.\n" +
                "Now go get ready to kill it, badass!\n\n" +
                "Good luck!", 
                player1.getUsername(),
                player2.getUsername())
        );

        // Send email to player2
        sendEmail(
            player2.getEmail(),
            "Knight to Meet You - Your Chess Match is Ready!",
            String.format(
                "Good day you stunning stack of sunshine %s,\n\n" +
                "You have been matched for a chess match.\n" +
                "It's game time and get ready to slay your tournament with %s.\n" +
                "Now go get ready to kill it, badass!\n\n" +
                "Good luck!",
                player2.getUsername(),
                player1.getUsername())
        );
    }

    public void sendRegisterNotification(Player player, String token) {
        String link = "http://localhost:8080/auth/verify?token=" + token;
        String content;
    
        if (player.isAdmin()) {
            content = String.format(
                "Greetings %s,\n\n" +
                "Welcome to Knight2MeetYou! We’re thrilled to have you join our community of passionate chess players from around the region.\n" +
                "You are signing up as an ADMIN.\n\n" +
                "Click here to verify your account: %s" +
                "\n\nBest,\nThe Knight2MeetYou Team",
                player.getUsername(),
                link  // Fourth parameter for the link
            );
        } else {
            content = String.format(
                "Greetings %s, Grandmaster-in-the-Making!\n\n" +
                "Welcome to Knight2MeetYou! We’re thrilled to have you join our community of passionate chess players from around the region.\n" +
                "Start by registering for tournaments. We’re excited to see you make your mark on the board.\n\n" +
                "Click here to verify your account: %s" +
                "\n\nBest,\nThe Knight2MeetYou Team",
                player.getUsername(),
                link  // Fourth parameter for the link
            );
        }
    
        sendEmail(player.getEmail(), "Welcome to Knight2MeetYou!", content);
    }
    

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
