package com.g5.cs203proj.controller;

import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.*;
// import com.g5.cs203proj.exception.*;
import com.g5.cs203proj.service.PlayerService;
import com.g5.cs203proj.service.TournamentService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

        // Check if the authenticated user is requesting their own data
        if (!authenticatedUsername.equals(username)) {
            throw new AccessDeniedException("You can only register yourself for a tournament.");
        }

        //
        Tournament updatedTournament = tournamentService.registerPlayer(playerId, tournamentId);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    /**
     * Remove a player from a tournament.
     */
    @DeleteMapping("/tournaments/{tournamentId}/players/{playerId}")
    public ResponseEntity<TournamentDTO> removePlayer(@PathVariable Long tournamentId, @PathVariable Long playerId) {
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

    /*
     * Start or cancel a tournament based on registration cutoff
     */
    @PutMapping("/tournaments/{id}/start-or-cancel")
    public ResponseEntity<TournamentDTO> startOrCancelTournament(@PathVariable Long id) {
        Tournament tournament = tournamentService.startOrCancelTournament(id);
        return new ResponseEntity<>(tournamentService.convertToDTO(tournament), HttpStatus.OK);
    }
    
    /*
     * Get tournament rankings by ID
     */
    @GetMapping("/tournaments/{id}/rankings")
    public ResponseEntity<Map<Long, Integer>> getTournamentRankings(@PathVariable Long id) {
        Map<Long, Integer> rankings = tournamentService.getTournamentRankings(id);
        return new ResponseEntity<>(rankings, HttpStatus.OK);
    }
}

