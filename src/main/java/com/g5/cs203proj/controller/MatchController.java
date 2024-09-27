package com.g5.cs203proj.controller;

import java.util.*;

import org.springframework.web.bind.annotation.RestController;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.service.MatchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class MatchController {
    private MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // create a new player
    @PostMapping("/matches")
    public Player createMatch(@RequestBody Player player) {
        return playerService.savePlayer(player);  // Persist the new player using playerService
    }
    

    // get the player
    @GetMapping("/players/{id}")
    public Player getPlayer(@PathVariable Long id) {
        Player player = playerService.getPlayerById(id);
        if (player == null) throw new PlayerNotFoundException(id);
        return player;
    }

    // get the username of the player
    @GetMapping("/players/{id}/username")
    public String getPlayerUsername(@PathVariable Long id ) {
        Player player = playerService.getPlayerById(id);
        if(player==null) throw new PlayerNotFoundException(id);
        return player.getUsername();
    }

    @GetMapping("/players/{id}/globalEloRating")
    public double getGlobalEloRating(@PathVariable Long id) {
        Player player = playerService.getPlayerById(id);
        if(player==null) throw new PlayerNotFoundException(id);
        return player.getGlobalEloRating();
    }
    

    // @PutMapping("players/{id}/username")
    // public Player updatePlayerUsername(@PathVariable Long id, @RequestParam String newUsername) {
    //     Player player = playerService.getPlayerById(id);
    //     if (player == null) throw new PlayerNotFoundException(id);
    //     player.setUsername(newUsername);
    //     playerService.savePlayer(player);
    //     return player;
    // }

    @PutMapping("/players/{id}")
    public Player updatePlayerAttributes(@PathVariable Long id, @RequestBody Map<String, String> updateFields) {
        Player player = playerService.getPlayerById(id);
        if (player == null) throw new PlayerNotFoundException(id);

        // Check for each key in the map and update the corresponding field
        if (updateFields.containsKey("username")) {
            player.setUsername(updateFields.get("username"));
        }

        if (updateFields.containsKey("globalEloRating")) {
            player.setGlobalEloRating(Double.parseDouble(updateFields.get("globalEloRating")));
        }

        playerService.savePlayer(player);  // Save the updated player
        return player;
    }



    
}
