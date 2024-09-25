package com.g5.cs203proj.controller;

import java.util.*;

import org.springframework.web.bind.annotation.RestController;

import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.service.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class PlayerController {
    private PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
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
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    public String getPlayerUsername(@PathVariable Long id ) {
        Player player = playerService.getPlayerById(id);
        if(player==null) throw new PlayerNotFoundException(id);
        return player.getUsername();
    }


    
}