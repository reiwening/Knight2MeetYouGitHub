package com.g5.cs203proj.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;

import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.DTO.PlayerDTO;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.player.PlayerAvailabilityException;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.service.PlayerDetailsService;
import com.g5.cs203proj.service.PlayerService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;



@Validated
@RestController
public class PlayerController {
    private PlayerService playerService;
    
    
    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
        
    }
    
    /**
     * Create a new player.
     */
    @PostMapping("/players")
    public ResponseEntity<?> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {

        // convert the DTO into a player 
        Player player = playerService.convertToEntity(playerDTO); // still has the raw password here

        // Check if the player already exists
        Player existingPlayer = playerService.registerPlayer(player); // registerPlayer() will hash the password 

        if (existingPlayer == null) {
            // Return a bad request or conflict status with a meaningful message
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        } /* else means that it is save to register this player */

        // Convert the registered Player entity back to PlayerDTO and return it
        PlayerDTO registeredPlayerDTO = playerService.convertToPlayerDTO(existingPlayer);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredPlayerDTO);
        
    }
    
    
    /**
     * Retrieve authenticated player's information by username.
     */
    @GetMapping("/players/{username}")
    public ResponseEntity<PlayerDTO>  getPlayer(@PathVariable String username) {
        
        playerService.validateUserAccess(username);

        Optional<Player> existingPlayer = playerService.findPlayerByUsername(username); 
        if(!existingPlayer.isPresent()) {
            throw new UsernameNotFoundException(username); // can do testing to see if this exception is thrown 
        }

        PlayerDTO playerDTO = playerService.convertToPlayerDTO(existingPlayer.get());
        return ResponseEntity.ok(playerDTO);
    }

    /**
     * Delete a player by username.
     */
    @DeleteMapping("/players/{username}")
    public String deletePlayer(@PathVariable String username) {
        playerService.deletePlayer(username);
        return "Player " + username + " deleted successfully";
    }

    /**
     * Get a list of all players.
     */
    @GetMapping("/players")
    public List<PlayerDTO>  getAllPlayers() {

        List<Player> players = playerService.getAllPlayers();
        return players.stream()
                        .map(player -> playerService.convertToPlayerDTO(player))
                        .collect(Collectors.toList());
    }

    /* 
     * Helper methods for updatePlayerAttributes() 
     */    
    private void checkNullAndEmptyFields(Map<String, String> updateFields) {
        if (updateFields == null || updateFields.isEmpty()) {
            throw new IllegalArgumentException("No fields to update");
        }
    }
    private void updateUsername(String newUsername, Player player) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        boolean usernameExists = playerService.getAllPlayers().stream()
            .filter(p -> p.getUsername() != null)
            .anyMatch(p -> p.getUsername().equals(newUsername));
    
        if (usernameExists) {
            throw new IllegalArgumentException(newUsername + " is already taken.");
        }
        player.setUsername(newUsername);
    }
    
    /**
     * Update player attributes 
     * Only the authenticated user can update their own data.
     */
    @PutMapping("/players")
    public PlayerDTO updatePlayerAttributes(@RequestParam String username, @RequestBody Map<String, String> updateFields) {

        playerService.validateUserAccess(username);

        Player player = playerService.findPlayerByUsername(username)
            .orElseThrow(() -> new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.NOT_FOUND));

        // Null check and prevent empty updates
        checkNullAndEmptyFields(updateFields);
        
        if (updateFields.containsKey("globalEloRating")) {
            throw new IllegalArgumentException("Haha, clever move! But modifying your own Elo rating? Dream on, my friend...");
        }

        // Check for each key in the map and update the corresponding field
        if (updateFields.containsKey("username")) {
            String newUsername = updateFields.get("username");
            updateUsername(newUsername, player);
        }

        playerService.savePlayer(player);  
        PlayerDTO updatedPlayerDTO = playerService.convertToPlayerDTO(player);
        return updatedPlayerDTO;
    
    }
}
   