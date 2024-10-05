package com.g5.cs203proj.controller;

import java.util.*;

import org.springframework.web.bind.annotation.RestController;

import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.PlayerNotFoundException;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.service.PlayerDetailsService;
import com.g5.cs203proj.service.PlayerService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;




@RestController
public class PlayerController {
    private PlayerService playerService;
    

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
        
    }


    // create a new player
    @PostMapping("/players")
    public ResponseEntity<?> createPlayer(@Valid @RequestBody Player player) {
            // Check if the player already exists
            Player existingPlayer = playerService.registerPlayer(player); 
            
            if (existingPlayer == null) {
            // Return a bad request or conflict status with a meaningful message
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        } /* else means that it is save to register this player */
        return ResponseEntity.ok(existingPlayer);  // Return the saved player with a 200 OK status
    }

    // Player would be able to retrieve his/her information when player inputs username
    @GetMapping("/players/{username}")
    public ResponseEntity<Player>  getPlayer(@PathVariable String username) {
        // Get the currently authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();  // The logged-in username

        // Check if the authenticated user is requesting their own data
        if (!authenticatedUsername.equals(username)) {
            throw new AccessDeniedException("You are trying to access data for Player: " + username);
        }

        Optional<Player> existingPlayer = playerService.findPlayerByUsername(username); 
if(!existingPlayer.isPresent()) {
    throw new UsernameNotFoundException(username);
}

        // If they are allowed and username in found in DB 
        Player player = existingPlayer.get();
        return ResponseEntity.ok(player);
    }

    @GetMapping("players")
    public List<Player>  getAllPlayers() {
        return playerService.getAllPlayers();
    }


}




///////////////////////////////////////////////////////////////////////////////////////////

    // // get the player
    // @GetMapping("/players/{id}")
    // public Player getPlayer(@PathVariable Long id) {
    //     Player player = playerService.getPlayerById(id);
    //     if (player == null) throw new PlayerNotFoundException(id);
    //     return player;
    // }

    // @GetMapping("/players/{id}/globalEloRating")
    // public double getGlobalEloRating(@PathVariable Long id) {
    //     Player player = playerService.getPlayerById(id);
    //     if(player==null) throw new PlayerNotFoundException(id);
    //     return player.getGlobalEloRating();
    // }
    

// @PutMapping("players/{id}/username")
// public Player updatePlayerUsername(@PathVariable Long id, @RequestParam String newUsername) {
//     Player player = playerService.getPlayerById(id);
//     if (player == null) throw new PlayerNotFoundException(id);
//     player.setUsername(newUsername);
//     playerService.savePlayer(player);
//     return player;
// }

    // @PutMapping("/players/{id}")
    // public Player updatePlayerAttributes(@PathVariable Long id, @RequestBody Map<String, String> updateFields) {
    //     Player player = playerService.getPlayerById(id);
    //     if (player == null) throw new PlayerNotFoundException(id);

    //     // Check for each key in the map and update the corresponding field
    //     if (updateFields.containsKey("username")) {
    //         player.setUsername(updateFields.get("username"));
    //     }

    //     if (updateFields.containsKey("globalEloRating")) {
    //         player.setGlobalEloRating(Double.parseDouble(updateFields.get("globalEloRating")));
    //     }

    //     playerService.savePlayer(player);  // Save the updated player
    //     return player;
    // }



    
// }