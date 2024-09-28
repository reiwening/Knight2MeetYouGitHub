package com.g5.cs203proj.controller;

import java.util.*;

import org.springframework.web.bind.annotation.RestController;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.service.MatchService;
import com.g5.cs203proj.service.PlayerService;

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
    public Match assignMatchPlayers(@PathVariable Long matchId, @RequestBody Player p1, @RequestBody Player p2) {
        //TODO: process PUT request
        Match match = matchService.findMatchById(matchId);
        if (match == null) throw new MatchNotFoundException(id);

        matchService.assignPlayersToMatch(match, p1, p2);
        matchService.saveMatch(match);
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
    @PutMapping("/matches/{id}")
    public Match updateMatchResults(@PathVariable Long id, @RequestBody Player winner) {
        Match match = matchService.findMatchById(id);
        if (match == null) throw new MatchNotFoundException(id);

        // call processMatchResult in MatchServiceImpl
        matchService.processMatchResult(match, winner);
        matchService.saveMatch(match);
        return match;
    }
}
