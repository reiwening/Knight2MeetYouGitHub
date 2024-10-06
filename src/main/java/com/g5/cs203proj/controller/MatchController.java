package com.g5.cs203proj.controller;

import java.util.*;

import org.springframework.web.bind.annotation.RestController;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.service.MatchService;
import com.g5.cs203proj.service.PlayerService;
import com.g5.cs203proj.exception.PlayerNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class MatchController {
    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // create a new match
    @PostMapping("/matches")
    public Match createMatch(@RequestBody Match match) {
        // Persist the new match using matchService
        return matchService.saveMatch(match);
    }
    


    // assign players to created match
    @PutMapping("matches/{id}")
    public Match assignMatchPlayers(@PathVariable Long id, @RequestBody Player player) {
        //TODO: process PUT request
        Match match = matchService.findMatchById(id);
        if (match == null) throw new MatchNotFoundException(id);

        Player managedPlayer = playerService.getPlayerById(player.getId());
        if (managedPlayer == null) throw new PlayerNotFoundException(player.getId());
        // System.out.println(managedPlayer.getUsername());

        matchService.assignPlayerToMatch(match, managedPlayer);
        matchService.saveMatch(match);

        playerService.addMatchToPlayerHistory(managedPlayer, match);
        playerService.savePlayer(managedPlayer);
        // System.out.println(managedPlayer.getUsername());
        return match;
    }

    // get the match
    @GetMapping("/matches/{id}")
    public Match getMatch(@PathVariable Long id) {
        Match match = matchService.findMatchById(id);
        if (match == null) throw new MatchNotFoundException(id);
        return match;
    }

    // process match when it ends
    @PutMapping("/matches/{id}/updateresults")
    public Match updateMatchResults(
        @PathVariable Long id, 
        @RequestParam boolean isDraw, 
        @RequestBody(required = false) Player winner) {
        
        Match match = matchService.findMatchById(id);
        if (match == null) throw new MatchNotFoundException(id);

        if (isDraw) {
            // Handle the draw scenario
            matchService.processMatchResult(match, null, true);
        } else {
            // Handle the winner scenario
            Player managedPlayer = playerService.getPlayerById(winner.getId());
            if (managedPlayer == null) throw new PlayerNotFoundException(winner.getId());
            matchService.processMatchResult(match, managedPlayer, false);
        }

        matchService.saveMatch(match);
        return match;
    }

}

