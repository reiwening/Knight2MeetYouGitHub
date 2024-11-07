package com.g5.cs203proj.controller;

import java.util.*;

import org.springframework.web.bind.annotation.RestController;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.service.TournamentService;
import com.g5.cs203proj.DTO.MatchDTO;
import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.service.MatchService;
import com.g5.cs203proj.service.PlayerService;
import com.g5.cs203proj.exception.*;
import com.g5.cs203proj.exception.match.MatchNotFoundException;
import com.g5.cs203proj.exception.player.PlayerAvailabilityException;
import com.g5.cs203proj.exception.tournament.TournamentNotFoundException;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Validated
@RestController
public class MatchController {
  
    private MatchService matchService;
    private PlayerService playerService;
    private TournamentService tournamentService;

    @Autowired
    public MatchController(MatchService matchService, PlayerService playerService, TournamentService tournamentService ) {
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.playerService = playerService;
    }

// test ok 
    // create a new match, given a tournament ID
    // havent assign my 2 players yet
    @PostMapping("/tournament/{id}/matches")
    public MatchDTO createMatchForTournament(@PathVariable Long id) {
        // Find the tournament by ID
        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament == null) {
            throw new TournamentNotFoundException(id);  // Custom exception
        }
        
        // Create a new Match with the specified tournament
        Match newMatch = new Match();
        newMatch.setTournament(tournament);

        // Add the new match to the tournament's match history
        tournament.getTournamentMatchHistory().add(newMatch);
        
        // Save the match in the database
        Match savedMatch = matchService.saveMatch(newMatch);

        // Save the updated tournament to include the new match
        tournamentService.updateTournament(id, tournament); // because we changed the match history 
        
        // Convert the saved Match entity back to DTO and return it
        MatchDTO savedMatchDTO = matchService.convertToDTO(savedMatch);
        return savedMatchDTO;
    }

    @PostMapping("/tournament/{tournamentId}/round-robin-matches")
    public List<MatchDTO> createRoundRobinMatches(@PathVariable Long tournamentId) {
        List<Match> matches = matchService.createRoundRobinMatches(tournamentId);
        return matches.stream().map(matchService::convertToDTO).collect(Collectors.toList());
    }

    //test: ok
    // delete a match from a tournament
    @DeleteMapping("/tournament/{tournamentId}/matches/{matchId}")
    public String deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return "Match " + matchId + " deleted successfully";
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
    
// test : ok
    // get the match
    @GetMapping("/matches/{matchId}")
    public MatchDTO getMatch(@PathVariable Long matchId) {
        Match match = matchService.findMatchById(matchId);
        if (match == null) throw new MatchNotFoundException(matchId);
        return matchService.convertToDTO(match);  
    }

// test : ok
    // assign 2 random players to a create match for a tournament 
    @PutMapping("/tournament/{tournamentId}/matches/{matchId}/random-players")
    public MatchDTO assignRandomPlayersToMatch(@PathVariable Long matchId){
       
        Match match = matchService.assignRandomPlayers(matchId);
        return matchService.convertToDTO(match);
    }


    // process match when it ends
    @PutMapping("/tournament/{tournamentId}/matches/{id}/updateresults")
    public MatchDTO updateMatchResults(
        @PathVariable Long id, 
        @RequestParam boolean isDraw, 
        @RequestBody(required = false) Player winner) {
        
        // check if match exists
        Match match = matchService.findMatchById(id);
        if (match == null) throw new MatchNotFoundException(id);

        // check if winner is a player in this match
        Long winnerId = winner.getId();
        if (!isDraw) {
            if (winnerId != match.getPlayer1().getId() && winnerId != match.getPlayer2().getId()) 
            throw new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.NOT_FOUND);
            // throw new InvalidMatchWinnerException("Winner must be a player in this match.");
        }
        if (isDraw) {
            matchService.processMatchResult(match, null, true);
        } else {
            Player managedPlayer = playerService.getPlayerById(winner.getId());
            if (managedPlayer == null) throw new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.NOT_FOUND);

            matchService.processMatchResult(match, managedPlayer, false);
        }
    
        matchService.saveMatch(match);
        return matchService.convertToDTO(match);  // Return MatchDTO instead of Match
    }

// test : ok 
    // get all matches for a particular tournament 
    @GetMapping("/tournament/{tournamentId}/matches")
    public List<MatchDTO> getAllMatchesForATournament(@PathVariable Long tournamentId) {

        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament.getTournamentMatchHistory() == null) {
            return new ArrayList<>();
        }
    
        return tournament.getTournamentMatchHistory().stream()
                         .map(matchService::convertToDTO)
                         .collect(Collectors.toList());
    }
    


}

