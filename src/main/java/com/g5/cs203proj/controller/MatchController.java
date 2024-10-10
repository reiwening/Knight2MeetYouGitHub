package com.g5.cs203proj.controller;

import java.util.*;

import org.springframework.web.bind.annotation.RestController;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.service.TournamentService;
import com.g5.cs203proj.DTO.MatchDTO;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.service.MatchService;
import com.g5.cs203proj.service.PlayerService;
import com.g5.cs203proj.exception.*;

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
    private TournamentService tournamentService;

    // @Autowired
    // public MatchController(MatchService matchService) {
    //     this.matchService = matchService;
    // }

    // create a new match, given a tournament ID
    // havent assign my 2 players yet
    @PostMapping("/matches/{tournamentId}")
    public MatchDTO createMatchForTournament(@PathVariable Long tournamentId) {
        // Find the tournament by ID
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament == null) {
            throw new TournamentNotFoundException(tournamentId);  // Custom exception
        }
        
        // Create a new Match with the specified tournament
        Match newMatch = new Match();
        newMatch.setTournament(tournament);
        
        // Save the match in the database
        Match savedMatch = matchService.saveMatch(newMatch);
        
        // Convert the saved Match entity back to DTO and return it
        MatchDTO savedMatchDTO = matchService.convertToDTO(savedMatch);
        return savedMatchDTO;
    }
// @PostMapping("/matches")
// public MatchDTO createMatch(@RequestBody MatchDTO matchDTO) {
//     // so that json body only needs a matchDTO
//     Match match = matchService.convertToEntity(matchDTO);
//     // save match in DB
//     Match savedMatch = matchService.saveMatch(match);
    
//     // Convert the saved Match entity back to MatchDTO to include any generated fields (like ID)
//     MatchDTO savedMatchDTO = matchService.convertToDTO(savedMatch);
//     return savedMatchDTO;

// }
    


    // assign 2 random players to a create match for a tournament 
    @PutMapping("matches/{id}/random-players")
    public MatchDTO assignRandomPlayersToMatch(@PathVariable Long id){
       
        Match match = matchService.assignRandomPlayers(id);
        return matchService.convertToDTO(match);
    }


    // get the match
    @GetMapping("/matches/{id}")
    public MatchDTO getMatch(@PathVariable Long id) {
        Match match = matchService.findMatchById(id);
        if (match == null) throw new MatchNotFoundException(id);
        return matchService.convertToDTO(match);  
    }

    // process match when it ends
    @PutMapping("/matches/{id}/updateresults")
    public MatchDTO updateMatchResults(
        @PathVariable Long id, 
        @RequestParam boolean isDraw, 
        @RequestBody(required = false) Player winner) {
        
        Match match = matchService.findMatchById(id);
        if (match == null) throw new MatchNotFoundException(id);
    
        if (isDraw) {
            matchService.processMatchResult(match, null, true);
        } else {
            Player managedPlayer = playerService.getPlayerById(winner.getId());
            if (managedPlayer == null) throw new PlayerNotFoundException(winner.getId());
            matchService.processMatchResult(match, managedPlayer, false);
        }
    
        matchService.saveMatch(match);
        return matchService.convertToDTO(match);  // Return MatchDTO instead of Match
    }

    // get all matches 

}

