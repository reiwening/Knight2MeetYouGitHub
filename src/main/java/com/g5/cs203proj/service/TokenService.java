package com.g5.cs203proj.service;

import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.VerificationToken;
import com.g5.cs203proj.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    // Method to generate a unique email verification token
    public String generateEmailVerificationToken(Player player) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, player, LocalDateTime.now().plusHours(24)); // Token expires in 24 hours
        tokenRepository.save(verificationToken);
        return token;
    }

    public boolean validateToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }
        return true;
    }

    public VerificationToken getTokenByToken(String token) {
        return tokenRepository.findByToken(token).orElseThrow(() -> new IllegalArgumentException("Token not found"));
    }
    
    
}
