package com.g5.cs203proj.controller;

import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.*;
// import com.g5.cs203proj.exception.*;
import com.g5.cs203proj.service.PlayerService;
import com.g5.cs203proj.service.TournamentService;
import com.g5.cs203proj.service.*;
import com.g5.cs203proj.exception.global.*;
import com.g5.cs203proj.exception.inputs.*;
import com.g5.cs203proj.exception.match.*;
import com.g5.cs203proj.exception.player.*;
import com.g5.cs203proj.exception.tournament.*;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Validated
@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private PlayerService playerService;

    public TournamentController(TournamentService tournamentService, PlayerService playerService){
        this.tournamentService = tournamentService;
        this.playerService = playerService;
    }
    
    /**
     * Create a new tournament.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tournaments")
    public ResponseEntity<TournamentDTO> createTournament(@Valid @RequestBody TournamentDTO tournamentDTO) {
        Tournament savedTournament = tournamentService.createTournament(tournamentService.convertToEntity(tournamentDTO));
        return new ResponseEntity<>(tournamentService.convertToDTO(savedTournament), HttpStatus.CREATED);
    }

    /**
     * Update an existing tournament by ID.
     */
    @PutMapping("/tournaments/{id}")
    public ResponseEntity<TournamentDTO> updateTournament(@PathVariable Long id, @RequestBody TournamentDTO updatedTournamentDTO) {
        Tournament updatedTournament = tournamentService.updateTournament(id, tournamentService.convertToEntity(updatedTournamentDTO));
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    /**
     * Delete a tournament by ID.
     */
    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<Map<String, String>> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        Map<String, String> body = new HashMap<>();
        body.put("Success", "Successfully deleted tournament " + id);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    /**
     * Get a specific tournament by ID.
     */
    @GetMapping("/tournaments/{id}")
    public ResponseEntity<TournamentDTO> getTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        return new ResponseEntity<>(tournamentService.convertToDTO(tournament), HttpStatus.OK);
    }


    /**
     * Get a list of all tournaments.
     */
    @GetMapping("/tournaments")
    public ResponseEntity<List<TournamentDTO>> getAllTournaments() {
        List<TournamentDTO> tournamentDTOs = tournamentService.getAllTournaments()
                .stream()
                .map(tournamentService::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(tournamentDTOs, HttpStatus.OK);
    }

    /**
     * Get a list of all tournaments that are open for registration.
     */
    @GetMapping("/tournaments/reg")
    public ResponseEntity<List<TournamentDTO>> getAllRegisterableTournaments() {
        List<TournamentDTO> tournamentDTOs = tournamentService.getAllRegisterableTournaments()
                .stream()
                .map(tournamentService::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(tournamentDTOs, HttpStatus.OK);
    }

//havent test yet
// Start or cancel a tournament based on registration cutoff
    @PutMapping("/tournaments/{id}/start-or-cancel")
    public ResponseEntity<TournamentDTO> startOrCancelTournament(@PathVariable Long id) {
        Tournament tournament = tournamentService.startOrCancelTournament(id);
        return new ResponseEntity<>(tournamentService.convertToDTO(tournament), HttpStatus.OK);
    }

    //havent tested
    // Get tournament rankings by ID
    @GetMapping("/tournaments/{id}/rankings")
    public ResponseEntity<Map<Long, Integer>> getTournamentRankings(@PathVariable Long id) {
        Map<Long, Integer> rankings = tournamentService.getTournamentRankings(id);
        return new ResponseEntity<>(rankings, HttpStatus.OK);
    }

    // test: ok (solo 8/11/24)
    // Register a player to a tournament
    /**
     * Register a player to a tournament.
     * Only the authenticated user can register themselves.
     */
    @PostMapping("/tournaments/{tournamentId}/players")
    public ResponseEntity<TournamentDTO> registerPlayer(@PathVariable Long tournamentId, @RequestParam Long playerId) {

        //ADDED ONLY PLAYER CAN ADD ITSELF TO A TOURNAMENT

        String username = playerService.getPlayerById(playerId).getUsername();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if authentication is null or not authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("You need authorisation to register for a tournament.");
        }

        // Check if the authenticated user is an ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            throw new AccessDeniedException("Admins are not allowed to register for tournaments.");
        }
        
        String authenticatedUsername = authentication.getName();  // The logged-in username
        playerService.validateUserAccess(authenticatedUsername);

        //
        Tournament updatedTournament = tournamentService.registerPlayer(playerId, tournamentId);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    //test: ok (solo 8/11/24)
    //remove player from a tournament
    /**
     * Remove a player from a tournament.
     */
    @DeleteMapping("/tournaments/{tournamentId}/players/{playerId}")
    public ResponseEntity<TournamentDTO> removePlayer(@PathVariable Long tournamentId, @PathVariable Long playerId) {
        // Debug logging to check the current username
        // System.out.println("Current authenticated user: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        
        //ADDED ONLY PLAYER CAN ADD ITSELF TO A TOURNAMENT
        String username = playerService.getPlayerById(playerId).getUsername();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if authentication is null or not authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("You need authorisation to leave a tournament.");
        }
        
        String authenticatedUsername = authentication.getName();  // The logged-in username

        // Check if the authenticated user is an ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // Check if the authenticated user is requesting their own data
        if (!isAdmin && !authenticatedUsername.equals(username)) {
            throw new AccessDeniedException("You can only remove yourself from a tournament.");
        }
        
        Tournament updatedTournament = tournamentService.removePlayer(playerId, tournamentId);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    /**
     * Get a list of players registered in a tournament by tournament ID.
     */
    @GetMapping("/tournaments/{id}/players")
    public ResponseEntity<Set<Player>> getRegisteredPlayers(@PathVariable Long id) {
        Set<Player> players = tournamentService.getRegisteredPlayers(id);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    /**
     * Update the Elo rating range for a tournament.
     */
    @PutMapping("/tournaments/{id}/elo-range")
    public ResponseEntity<TournamentDTO> setTournamentEloRange(@PathVariable Long id, @RequestParam int minElo, @RequestParam int maxElo) {
        Tournament updatedTournament = tournamentService.setTournamentEloRange(id, minElo, maxElo);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }
    
     /**
     * Update the tournament status.
     */
    @PutMapping("/tournaments/{id}/status")
    public ResponseEntity<TournamentDTO> setTournamentStatus(@PathVariable Long id, @RequestParam String status) {
        Tournament updatedTournament = tournamentService.setTournamentStatus(id, status);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }
    
    /**
     * Update the tournament style.
     */
    @PutMapping("/tournaments/{id}/style")
    public ResponseEntity<TournamentDTO> setTournamentStyle(@PathVariable Long id, @RequestParam String style) {
        Tournament updatedTournament = tournamentService.setTournamentStyle(id, style);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    /**
     * Update the player range (min and max players) for a tournament.
     */
    @PutMapping("/tournaments/{id}/player-range")
    public ResponseEntity<TournamentDTO> setTournamentPlayerRange(
        @PathVariable Long id, @RequestParam int minPlayers, @RequestParam int maxPlayers) {
        Tournament updatedTournament = tournamentService.setTournamentPlayerRange(id, minPlayers, maxPlayers);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    /**
     * Update the registration cutoff time for a tournament.
     */
    @PutMapping("/tournaments/{id}/registration-cutoff")
    public ResponseEntity<TournamentDTO> setTournamentRegistrationCutOff(
        @PathVariable Long id, @RequestParam int year,  @RequestParam int monthOfYear, 
        @RequestParam int dayOfMonth, @RequestParam int minute, @RequestParam int hour) {
        LocalDateTime registrationCutOff = LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute);
        Tournament updatedTournament = tournamentService.setTournamentRegistrationCutOff(id, registrationCutOff);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    /**
     * Update the name of a tournament.
     */
    @PutMapping("/tournaments/{id}/name")
    public ResponseEntity<TournamentDTO> setName(@PathVariable Long id, @RequestParam String newName) {
        Tournament updatedTournament = tournamentService.setName(id, newName);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    //test: done
    // Get all matches in a tournaments with players, winner, elo change
    @GetMapping("/tournaments/{tournamentId}/matches")
    public List<ArrayList<String>> getTournamentMatches(@PathVariable Long tournamentId) {
        return tournamentService.getTournamentMatchHistory(tournamentId);
    }

    //test: working but persists test match twice for some reason
    // Add a test match to a tournament
    @PostMapping("/tournaments/{tournamentId}/matches")
    public Tournament testPostMatch(@PathVariable Long tournamentId, @RequestBody Match match) {
        //TODO: process POST request
        tournamentService.addTestMatchToTournament(tournamentId, match);
        return tournamentService.getTournamentById(tournamentId);
    }
}

