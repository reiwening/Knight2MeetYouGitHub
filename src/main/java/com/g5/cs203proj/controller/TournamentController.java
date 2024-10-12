package com.g5.cs203proj.controller;

import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.exception.*;
import com.g5.cs203proj.service.PlayerService;
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

    @Autowired
    private PlayerService playerService;

    public TournamentController(TournamentService tournamentService, PlayerService playerService){
        this.tournamentService = tournamentService;
        this.playerService = playerService;
    }

    //test: ok (9/10/24)
    // Create a new tournament
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tournaments")
    public Tournament createTournament(@RequestBody Tournament tournament) {
        return tournamentService.createTournament(tournament);
    }

    //test: ok (9/10/24)
    // Get a specific tournament by ID
    @GetMapping("/tournaments/{id}")
    public Tournament getTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament == null){
            throw new TournamentNotFoundException(id);
        }
        return tournament;
    }

    //test: ok (9/10/24)
    // Update a tournament by ID
    @PutMapping("/tournaments/{id}")
    public Tournament updateTournament(@PathVariable Long id, @RequestBody Tournament updatedTournament) {
        Tournament tournament = getTournamentById(id);
        return tournamentService.updateTournament(tournament, updatedTournament);
    }

    //test: ok (9/10/24)
    // Delete a tournament by ID
    @DeleteMapping("/tournaments/{id}")
    public void deleteTournament(@PathVariable Long id) {
        Tournament tournament = getTournamentById(id);
        tournamentService.deleteTournament(tournament);
    }

    //test: ok (9/10/24)
    // Get all tournaments
    @GetMapping("/tournaments")
    public List<Tournament> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    //test: ok (9/10/24)
    // Get all registerable tournaments
    @GetMapping("/tournaments/Registration")
    public List<Tournament> getAllRegisterableTournaments() {
        return tournamentService.getAllRegisterableTournaments();
    }

    //havent test yet
    // Start or cancel a tournament based on registration cutoff
    @PostMapping("/tournaments/{id}")
    public Tournament startOrCancelTournament(@PathVariable Long id) {
        Tournament tournament = getTournamentById(id);
        //check if tournament is in Registration status
        if (tournament.getTournamentStatus() != "Registration"){
            throw new TournamentNotInRegistrationException("Tournament not in \"Registration\" status");
        }
        return tournamentService.startOrCancelTournament(tournament);
    }

    //havent tested
    // Get tournament rankings by ID
    @GetMapping("/tournaments/{id}/rankings")
    public Map<Long, Integer> getTournamentRankings(@PathVariable Long id) {
        Tournament tournament = getTournamentById(id);
        return tournamentService.getTournamentRankings(tournament);
    }

    //test: ok (9/10/24)
    //do i need to check for adding player after registration close?
    // Register a player to a tournament
    @PostMapping("/tournaments/{tournamentId}/players")
    public Tournament registerPlayer(@PathVariable Long tournamentId, @RequestParam Long playerId) {
        Tournament tournament = getTournamentById(tournamentId);
        Player player = playerService.getPlayerById(playerId);
        //check if player is alr in tourn
        List<Player> registeredPlayers = tournament.getRegisteredPlayers();
        if (registeredPlayers.contains(player)){
            throw new PlayerAlreadyInTournamentException(playerId, tournamentId);
        }
        //check if adding will exceed player limit
        if (registeredPlayers.size() >= tournament.getMaxPlayers()){
            throw new TournamentFullException(tournamentId);
        }
        //pass both checks, can add player
        return tournamentService.registerPlayer(player, tournament);
    }

    //test: ok (9/10/24)
    // Remove a player from a tournament
    @DeleteMapping("/tournaments/{tournamentId}/players/{playerId}")
    public Tournament removePlayer(@PathVariable Long tournamentId, @PathVariable Long playerId) {
        Tournament tournament = getTournamentById(tournamentId);
        Player player = playerService.getPlayerById(playerId);
        //check if player is not in tournament
        List<Player> registeredPlayers = tournament.getRegisteredPlayers();
        if (!registeredPlayers.contains(player)){
            throw new PlayerNotInTournamentException(playerId, tournamentId);
        }
        //save to add to tourn
        return tournamentService.removePlayer(player, tournament);
    }

    //test: ok (9/10/24)
    // Get a list of registered players in a tournament
    @GetMapping("/tournaments/{id}/players")
    public List<Player> getRegisteredPlayers(@PathVariable Long id) {
        Tournament tournament = getTournamentById(id);
        return tournamentService.getRegisteredPlayers(tournament);
    }

    //test: ok (9/10/24)
//TODO: check if the players already in the tourn adhere to the new elo range if not dont allow change
    // Update the Elo range for the tournament
    @PutMapping("/tournaments/{id}/elo-range") //not sure about mapping
    public Tournament setTournamentEloRange(@PathVariable Long id, @RequestParam int minElo, @RequestParam int maxElo) {
        Tournament tournament = getTournamentById(id);
        //check for negative elo values
        if (minElo < 0 || maxElo < 0){
            throw new InvalidEloValueException("Elo values cannot be negative");
        }
        //check for min elo > max elo
        if (minElo > maxElo){
            throw new InvalidEloValueException("minElo cannot be greater than maxElo");
        }
        //safe to change elo value
        return tournamentService.setTournamentEloRange(tournament, minElo, maxElo);
    }

    //test: ok (9/10/24)
//TODO: validate that status input is valid
    // Update the tournament status
    @PutMapping("/tournaments/{id}/status")
    public Tournament setTournamentStatus(@PathVariable Long id, @RequestParam String status) {
        Tournament tournament = getTournamentById(id);
        //need to check for invalid status?
        return tournamentService.setTournamentStatus(tournament, status);
    }

    //test: ok (9/10/24)
//TODO: validate that style input is valid
    // Update the tournament style
    @PutMapping("/tournaments/{id}/style")
    public Tournament setTournamentStyle(@PathVariable Long id, @RequestParam String style) {
        Tournament tournament = getTournamentById(id);
        return tournamentService.setTournamentStyle(tournament, style);
    }

    //test: ok (9/10/24)
    // Update the player range (min/max players)
    @PutMapping("/tournaments/{id}/player-range")
    public Tournament setTournamentPlayerRange(
        @PathVariable Long id, @RequestParam int minPlayers, @RequestParam int maxPlayers) {
        Tournament tournament = getTournamentById(id);
        //check for invalid player range
        if (minPlayers < 0 || maxPlayers < 0){
            throw new InvalidPlayerRangeException("Player count cannot be negative");
        }
        //check for min players > max players
        if (minPlayers > maxPlayers){
            throw new InvalidPlayerRangeException("minPlayers cannot be greater than maxPlayers");
        }
        //check if updated range can hold current player count
        int playerCount = tournament.getRegisteredPlayers().size();
        if (playerCount > maxPlayers){
            throw new InvalidPlayerRangeException("Tournament has more players than maxPlayers");
        }

        //safe to update player count
        return tournamentService.setTournamentPlayerRange(tournament, minPlayers, maxPlayers);
    }

    //test: ok (9/10/24)
    // Update the registration cutoff time
    @PutMapping("/tournaments/{id}/registration-cutoff")
    public Tournament setTournamentRegistrationCutOff(
        @PathVariable Long id, @RequestParam int year, @RequestParam int monthOfYear, @RequestParam int dayOfMonth, @RequestParam int hour, @RequestParam int minute) {
        Tournament tournament = getTournamentById(id);
        LocalDateTime registrationCutOff = LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute);
        return tournamentService.setTournamentRegistrationCutOff(tournament, registrationCutOff);
    }

    //test: not done (might need to change this to use new security config)
    // Update the tournament admin
    @PutMapping("/tournaments/{id}/admin")
    public Tournament setAdmin(
        @PathVariable Long id, @RequestBody Admin newAdmin) {
        Tournament tournament = getTournamentById(id);
        return tournamentService.setAdmin(tournament, newAdmin);
    }

    //test: ok (9/10/24)
    // Update the tournament name
    @PutMapping("/tournaments/{id}/name")
    public Tournament setName(
        @PathVariable Long id, @RequestParam String newName) {
        Tournament tournament = getTournamentById(id);
        return tournamentService.setName(tournament, newName);
    }
}

