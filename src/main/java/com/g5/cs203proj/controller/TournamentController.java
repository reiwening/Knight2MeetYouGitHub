package com.g5.cs203proj.controller;

import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService){
        this.tournamentService = tournamentService;
    }

    //test: ok
    // Create a new tournament
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tournaments")
    public TournamentDTO createTournament(@RequestBody TournamentDTO tournamentDTO) {
        Tournament tournament = tournamentService.convertToEntity(tournamentDTO);
        Tournament savedTournament = tournamentService.createTournament(tournament);
        TournamentDTO savedTournamentDTO = tournamentService.convertToDTO(savedTournament);
        return savedTournamentDTO;
    }

    //test: ok
    // Update a tournament by ID
    @PutMapping("/tournaments/{id}")
    public TournamentDTO updateTournament(
        @PathVariable Long id, @RequestBody TournamentDTO updatedTournamentDTO) {
        Tournament updatedTournament = tournamentService.convertToEntity(updatedTournamentDTO);
        Tournament savedTournament = tournamentService.updateTournament(id, updatedTournament);
        return tournamentService.convertToDTO(savedTournament); // return DTO
    }

    //test: ok
    // Delete a tournament by ID
    @DeleteMapping("/tournaments/{id}")
    public void deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
    }

    //test: ok
    // Get a specific tournament by ID
    @GetMapping("/tournaments/{id}")
    public TournamentDTO getTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        return tournamentService.convertToDTO(tournament); // return DTO
    }


    //test: ok
    // Get all tournaments
    @GetMapping("/tournaments")
    public List<TournamentDTO> getAllTournaments() {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        return tournaments.stream()
                        .map(tournamentService::convertToDTO) // Convert to DTO
                        .collect(Collectors.toList());
    }

    //test: ok
    // Get all registerable tournaments
    @GetMapping("/tournaments/reg")
    public List<TournamentDTO> getAllRegisterableTournaments() {
        List<Tournament> tournaments = tournamentService.getAllRegisterableTournaments();
        return tournaments.stream()
                        .map(tournamentService::convertToDTO) // Convert to DTO
                        .collect(Collectors.toList());
    }

//havent test yet
    // Start or cancel a tournament based on registration cutoff
    @PostMapping("/tournaments/{id}")
    public TournamentDTO startOrCancelTournament(@PathVariable Long id) {
        Tournament tournament = tournamentService.startOrCancelTournament(id);
        return tournamentService.convertToDTO(tournament); // return DTO

    }

//havent tested
    // Get tournament rankings by ID
    @GetMapping("/tournaments/{id}/rankings") //not sure of this is mapping
    public Map<Long, Integer> getTournamentRankings(@PathVariable Long id) {
        return tournamentService.getTournamentRankings(id);
    }

    // test: later !! (reiwen)
    // CHECK FOR THE DIFFERENT LOGIC TO REGISTER A PLAYER
    // Register a player to a tournament
    @PostMapping("/tournaments/{tournamentId}/players")
    public TournamentDTO registerPlayer(@PathVariable Long tournamentId, @RequestParam Long playerId) {
        Tournament updatedTournament = tournamentService.registerPlayer(playerId, tournamentId);
        return tournamentService.convertToDTO(updatedTournament); // return DTO
    }

    //test: ok
    // Remove a player from a tournament
    @DeleteMapping("/tournaments/{tournamentId}/players/{playerId}")
    public TournamentDTO removePlayer(@PathVariable Long tournamentId, @PathVariable Long playerId) {
        Tournament updatedTournament = tournamentService.removePlayer(playerId, tournamentId);
        return tournamentService.convertToDTO(updatedTournament); // return DTO
    }

    //test: ok
    // Get a list of registered players in a tournament
    @GetMapping("/tournaments/{id}/players")
    public Set<Player> getRegisteredPlayers(@PathVariable Long id) {
        return tournamentService.getRegisteredPlayers(id);
    }

    //test: ok
    // Update the Elo range for the tournament
    @PutMapping("/tournaments/{id}/elo-range")
    public TournamentDTO setTournamentEloRange(
        @PathVariable Long id, @RequestParam int minElo, @RequestParam int maxElo) {
        Tournament updatedTournament = tournamentService.setTournamentEloRange(id, minElo, maxElo);
        return tournamentService.convertToDTO(updatedTournament); // return DTO
    }

    
    
    //test: ok
    // Update the tournament status
    @PutMapping("/tournaments/{id}/status")
    public TournamentDTO setTournamentStatus(@PathVariable Long id, @RequestParam String status) {
        Tournament updatedTournament = tournamentService.setTournamentStatus(id, status);
        return tournamentService.convertToDTO(updatedTournament); // return DTO
    }
        
    //test: ok
    // Update the tournament style
    @PutMapping("/tournaments/{id}/style")
    public TournamentDTO setTournamentStyle(
        @PathVariable Long id, @RequestParam String style) {
        Tournament updatedTournament = tournamentService.setTournamentStyle(id, style);
        return tournamentService.convertToDTO(updatedTournament); // return DTO
    }


    // test: ok
    // Update the player range (min/max players)
    @PutMapping("/tournaments/{id}/player-range")
    public TournamentDTO setTournamentPlayerRange(
        @PathVariable Long id, @RequestParam int minPlayers, @RequestParam int maxPlayers) {
        Tournament updatedTournament = tournamentService.setTournamentPlayerRange(id, minPlayers, maxPlayers);
        return tournamentService.convertToDTO(updatedTournament); // return DTO
    }


    //test later
    // Update the registration cutoff time
    @PutMapping("/tournaments/{id}/registration-cutoff")
    public TournamentDTO setTournamentRegistrationCutOff(
        @PathVariable Long id, @RequestParam int day, @RequestParam int month, 
        @RequestParam int year, @RequestParam int minute, @RequestParam int hour) {
        LocalDateTime registrationCutOff = LocalDateTime.of(year, month, day, hour, minute);
        Tournament updatedTournament = tournamentService.setTournamentRegistrationCutOff(id, registrationCutOff);
        return tournamentService.convertToDTO(updatedTournament); // return DTO
    }


    //test later, NOT SURE ABT ADMIN 
    // Update the tournament admin
@PutMapping("/tournaments/{id}/admin")
public Tournament setAdmin(
    @PathVariable Long id, @RequestBody Admin newAdmin) {
    return tournamentService.setAdmin(id, newAdmin);
}

    // test 
    @PutMapping("/tournaments/{id}/name")
    public TournamentDTO setName(
        @PathVariable Long id, @RequestParam String newName) {
        Tournament updatedTournament = tournamentService.setName(id, newName);
        return tournamentService.convertToDTO(updatedTournament); // return DTO
    }
    
}

