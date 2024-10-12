package com.g5.cs203proj.controller;

import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.service.TournamentService;
import com.g5.cs203proj.exception.*;
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
    //test: ok (matt 13/10/24)
    // Create a new tournament
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tournaments")
    public ResponseEntity<TournamentDTO> createTournament(@RequestBody TournamentDTO tournamentDTO) {
        Tournament savedTournament = tournamentService.createTournament(tournamentService.convertToEntity(tournamentDTO));
        return new ResponseEntity<>(tournamentService.convertToDTO(savedTournament), HttpStatus.CREATED);
    }

    //test: ok but no field validation (matt 13/10/24)
    // Update a tournament by ID
    @PutMapping("/tournaments/{id}")
    public ResponseEntity<TournamentDTO> updateTournament(@PathVariable Long id, @RequestBody TournamentDTO updatedTournamentDTO) {
        Tournament updatedTournament = tournamentService.updateTournament(id, tournamentService.convertToEntity(updatedTournamentDTO));
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    //test: ok (matt 13/10/24)
    // Delete a tournament by ID
    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return new ResponseEntity<>("Successfully deleted tournament " + id, HttpStatus.NO_CONTENT);
    }

    //test: ok (matt 13/10/24)
    // Get a specific tournament by ID
    @GetMapping("/tournaments/{id}")
    public ResponseEntity<TournamentDTO> getTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        return new ResponseEntity<>(tournamentService.convertToDTO(tournament), HttpStatus.OK);
    }


    //test: ok (matt 13/10/24)
    // Get all tournaments
    @GetMapping("/tournaments")
    public ResponseEntity<List<TournamentDTO>> getAllTournaments() {
        List<TournamentDTO> tournamentDTOs = tournamentService.getAllTournaments()
                .stream()
                .map(tournamentService::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(tournamentDTOs, HttpStatus.OK);
    }
    //test: ok (matt 13/10/24)
    // Get all registerable tournaments
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
    @PostMapping("/tournaments/{id}/start-or-cancel")
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

    // test: ok (matt 13/10/24)
    // Register a player to a tournament
    @PostMapping("/tournaments/{tournamentId}/players")
    public ResponseEntity<TournamentDTO> registerPlayer(@PathVariable Long tournamentId, @RequestParam Long playerId) {
        Tournament updatedTournament = tournamentService.registerPlayer(playerId, tournamentId);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    //test: ok (matt 13/10/24)
    //remove player from a tournament
    @DeleteMapping("/tournaments/{tournamentId}/players/{playerId}")
    public ResponseEntity<TournamentDTO> removePlayer(@PathVariable Long tournamentId, @PathVariable Long playerId) {
        Tournament updatedTournament = tournamentService.removePlayer(playerId, tournamentId);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    //test: ok but need matchDTO to not keep recurring (matt 13/10/24)
    // Get a list of registered players in a tournament
    @GetMapping("/tournaments/{id}/players")
    public ResponseEntity<Set<Player>> getRegisteredPlayers(@PathVariable Long id) {
        Set<Player> players = tournamentService.getRegisteredPlayers(id);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    //test: ok (matt 13/10/24)
    // Update the Elo range for the tournament
    @PutMapping("/tournaments/{id}/elo-range")
    public ResponseEntity<TournamentDTO> setTournamentEloRange(@PathVariable Long id, @RequestParam int minElo, @RequestParam int maxElo) {
        Tournament updatedTournament = tournamentService.setTournamentEloRange(id, minElo, maxElo);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }
    
    //test: ok (matt 13/10/24)
    // Update the tournament status
    @PutMapping("/tournaments/{id}/status")
    public ResponseEntity<TournamentDTO> setTournamentStatus(@PathVariable Long id, @RequestParam String status) {
        Tournament updatedTournament = tournamentService.setTournamentStatus(id, status);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }
    
    //test: ok (matt 13/10/24)
    // Update the tournament style
    @PutMapping("/tournaments/{id}/style")
    public ResponseEntity<TournamentDTO> setTournamentStyle(@PathVariable Long id, @RequestParam String style) {
        Tournament updatedTournament = tournamentService.setTournamentStyle(id, style);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    //Test: ok (matt 13/10/24)
    // Update the player range (min/max players)
    @PutMapping("/tournaments/{id}/player-range")
    public ResponseEntity<TournamentDTO> setTournamentPlayerRange(
        @PathVariable Long id, @RequestParam int minPlayers, @RequestParam int maxPlayers) {
        Tournament updatedTournament = tournamentService.setTournamentPlayerRange(id, minPlayers, maxPlayers);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    //test: ok (matt 13/10/24)
    // Update the registration cutoff time
    @PutMapping("/tournaments/{id}/registration-cutoff")
    public ResponseEntity<TournamentDTO> setTournamentRegistrationCutOff(
        @PathVariable Long id, @RequestParam int year,  @RequestParam int monthOfYear, 
        @RequestParam int dayOfMonth, @RequestParam int minute, @RequestParam int hour) {
        LocalDateTime registrationCutOff = LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute);
        Tournament updatedTournament = tournamentService.setTournamentRegistrationCutOff(id, registrationCutOff);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    //test later, NOT SURE ABT ADMIN 
    // Update the tournament admin
    @PutMapping("/tournaments/{id}/admin")
    public ResponseEntity<TournamentDTO> setAdmin(@PathVariable Long id, @RequestBody Admin newAdmin) {
        Tournament updatedTournament = tournamentService.setAdmin(id, newAdmin);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }

    //test: ok (matt 13/10/24)
    // Update the tournament name
    @PutMapping("/tournaments/{id}/name")
    public ResponseEntity<TournamentDTO> setName(@PathVariable Long id, @RequestParam String newName) {
        Tournament updatedTournament = tournamentService.setName(id, newName);
        return new ResponseEntity<>(tournamentService.convertToDTO(updatedTournament), HttpStatus.OK);
    }
}

