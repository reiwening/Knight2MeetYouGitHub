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

    /* 
     * Helper method to check if the match exists
    */
    private Match findMatchOrThrow(Long matchId) {
        Match match = matchService.findMatchById(matchId);
        if (match == null) {
            throw new MatchNotFoundException(matchId);
        }
        return match;   
    }


    /**
     * Create a new match for a specified tournament.
     */
    @PostMapping("/tournament/{id}/matches")
    public MatchDTO createMatchForTournament(@PathVariable Long id) {
    
        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament == null) {
            throw new TournamentNotFoundException(id);  
        }
 
        Match newMatch = new Match();
        newMatch.setTournament(tournament);

        tournament.getTournamentMatchHistory().add(newMatch);
        
        Match savedMatch = matchService.saveMatch(newMatch);

        tournamentService.updateTournament(id, tournament); 
        
        MatchDTO savedMatchDTO = matchService.convertToDTO(savedMatch);
        return savedMatchDTO;
    }

    /**
     * Create round-robin matches for a specified tournament.
     */
    @PostMapping("/tournament/{tournamentId}/round-robin-matches")
    public List<MatchDTO> createRoundRobinMatches(@PathVariable Long tournamentId) {
        List<Match> matches = matchService.createRoundRobinMatches(tournamentId);
        return matches.stream().map(matchService::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Delete a specific match from a tournament.
     */
    @DeleteMapping("/tournament/{tournamentId}/matches/{matchId}")
    public String deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return "Match " + matchId + " deleted successfully";
    }
    
    /**
     * Get a specific match by match ID.
     */
    @GetMapping("/matches/{matchId}")
    public MatchDTO getMatch(@PathVariable Long matchId) {
        Match match = findMatchOrThrow(matchId);
        return matchService.convertToDTO(match);  
    }

    /** 
     * Assign two random players to a match within a tournament.
     */
    @PutMapping("/tournament/{tournamentId}/matches/{matchId}/random-players")
    public MatchDTO assignRandomPlayersToMatch(@PathVariable Long matchId){
       
        Match match = matchService.assignRandomPlayers(matchId);
        return matchService.convertToDTO(match);
    }


     /**
     * Process match results and update the match status.
     * 
     * @param isDraw Specifies if the match ended in a draw.
     * @param winner The player who won the match (null if it’s a draw).
     */
    @PutMapping("/tournament/{tournamentId}/matches/{id}/updateresults")
    public MatchDTO updateMatchResults(
        @PathVariable Long tournamentId,
        @PathVariable Long id, 
        @RequestParam boolean isDraw, 
        @RequestBody(required = false) Player winner) {
        
        Match match = findMatchOrThrow(id);

        // check if winner is a player in this match
        Long winnerId = winner.getId();
        if (!isDraw) {
            if (winnerId != match.getPlayer1().getId() && winnerId != match.getPlayer2().getId()) 
            throw new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.NOT_FOUND);
        }
        if (isDraw) {
            // Process current match results
            matchService.processMatchResult(match, null, true);

            // Call createMatchForTournament logic if it's a draw
            MatchDTO newMatchDTO = createMatchForTournament(tournamentId);
            Long newMatchId = newMatchDTO.getId();
            matchService.reassignPlayersToMatch(id, newMatchId);
        } else {
            Player managedPlayer = playerService.getPlayerById(winnerId);
            if (managedPlayer == null) throw new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.NOT_FOUND);

            matchService.processMatchResult(match, managedPlayer, false);
        }
    
        matchService.saveMatch(match);
        return matchService.convertToDTO(match);  // Return MatchDTO instead of Match
    }


    /**
     * Get all matches for a specified tournament.
     */
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


    // get check-in status of both players
    @GetMapping("/matches/{id}/getcheckinstatus")
    public HashMap<String, Boolean> getCheckInStatus(@PathVariable Long id) {
        Match m = matchService.findMatchById(id);
        return matchService.viewCheckedInStatus(m);
    }
    
}

