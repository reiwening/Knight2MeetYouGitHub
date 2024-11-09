package com.g5.cs203proj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.g5.cs203proj.entity.Player;

import com.g5.cs203proj.entity.VerificationToken;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.service.TokenService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token) {
        try {
            boolean isValid = tokenService.validateToken(token);
            if (isValid) {
                // Activate user account if valid
                VerificationToken verificationToken = tokenService.getTokenByToken(token);
                Player player = verificationToken.getPlayer();
                player.setEnabled(true); // Mark the user as enabled/verified
                playerRepository.save(player);
                return "Account verified! You can now log in.";
            }
        } catch (IllegalArgumentException e) {
            return "Invalid or expired token.";
        }
        return "Error verifying account.";
    }
}