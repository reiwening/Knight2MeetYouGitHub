package com.g5.cs203proj.controller;

import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService){
        this.tournamentService = tournamentService;
    }

    // Create a new tournament
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tournaments")
    public Tournament createTournament(@RequestBody Tournament tournament) {
        return tournamentService.createTournament(tournament);
    }

    // Update a tournament by ID
    @PutMapping("/tournaments/{id}")
    public Tournament updateTournament(
        @PathVariable Long id, @RequestBody Tournament updatedTournament) {
        return tournamentService.updateTournament(id, updatedTournament);
    }

    // Delete a tournament by ID
    @DeleteMapping("/tournaments/{id}")
    public void deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
    }

    // Get a specific tournament by ID
    @GetMapping("/tournaments/{id}")
    public Tournament getTournamentById(@PathVariable Long id) {
        return tournamentService.getTournamentById(id);
    }

    // Get all tournaments
    @GetMapping("/tournaments")
    public List<Tournament> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    // Get all registerable tournaments
    @GetMapping("/tournaments/reg")
    public List<Tournament> getAllRegisterableTournaments() {
        return tournamentService.getAllRegisterableTournaments();
    }

    // Start or cancel a tournament based on registration cutoff
    @PostMapping("/tournaments/{id}")
    public Tournament startOrCancelTournament(@PathVariable Long id) {
        return tournamentService.startOrCancelTournament(id);
    }

    // Get tournament rankings by ID
    @GetMapping("/tournaments/{id}/rankings") //not sure of this is mapping
    public Map<Long, Integer> getTournamentRankings(@PathVariable Long id) {
        return tournamentService.getTournamentRankings(id);
    }

    // Register a player to a tournament
    @PostMapping("/tournaments/{tournamentId}/players")
    public Tournament registerPlayer(@PathVariable Long tournamentId, @RequestBody Long playerId) {
        return tournamentService.registerPlayer(playerId, tournamentId);
    }

    // Remove a player from a tournament
    @DeleteMapping("/tournaments/{tournamentId}/players/{playerId}")
    public Tournament removePlayer(@PathVariable Long tournamentId, @PathVariable Long playerId) {
        return tournamentService.removePlayer(playerId, tournamentId);
    }

    // Get a list of registered players in a tournament
    @GetMapping("/tournaments/{id}/players")
    public List<Player> getRegisteredPlayers(@PathVariable Long id) {
        return tournamentService.getRegisteredPlayers(id);
    }

    // Update the Elo range for the tournament
    @PutMapping("/tournaments/{id}/elo-range") //not sure about mapping
    public Tournament setTournamentEloRange(
        @PathVariable Long id, @RequestParam int minElo, @RequestParam int maxElo) {
        return tournamentService.setTournamentEloRange(id, minElo, maxElo);
    }

    // Update the tournament status
    @PutMapping("/tournaments/{id}/status")
    public Tournament setTournamentStatus(
        @PathVariable Long id, @RequestParam String status) {
        return tournamentService.setTournamentStatus(id, status);
    }

    // Update the tournament style
    @PutMapping("/tournaments/{id}/style")
    public Tournament setTournamentStyle(
        @PathVariable Long id, @RequestParam String style) {
        return tournamentService.setTournamentStyle(id, style);
    }

    // Update the player range (min/max players)
    @PutMapping("/tournaments/{id}/player-range")
    public Tournament setTournamentPlayerRange(
        @PathVariable Long id, @RequestParam int minPlayers, @RequestParam int maxPlayers) {
        return tournamentService.setTournamentPlayerRange(id, minPlayers, maxPlayers);
    }

    // Update the registration cutoff time
    @PutMapping("/tournaments/{id}/registration-cutoff")
    public Tournament setTournamentRegistrationCutOff(
        @PathVariable Long id, @RequestParam LocalDateTime registrationCutOff) {
        return tournamentService.setTournamentRegistrationCutOff(id, registrationCutOff);
    }

    // Update the tournament admin
    @PutMapping("/tournaments/{id}/admin")
    public Tournament setAdmin(
        @PathVariable Long id, @RequestBody Admin newAdmin) {
        return tournamentService.setAdmin(id, newAdmin);
    }

    // Update the tournament name
    @PutMapping("/tournaments/{id}/name")
    public Tournament setName(
        @PathVariable Long id, @RequestParam String newName) {
        return tournamentService.setName(id, newName);
    }
}

