package com.g5.cs203proj.service;

import java.time.LocalDateTime;
import java.util.*;


import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.Admin;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;

public interface TournamentService {
//Tournament cycle
    Tournament createTournament(Tournament tournament);
    Tournament updateTournament(Long tournamentId, Tournament updatedTournament);
    void deleteTournament(Long tournamentId);
    Tournament getTournamentById(Long tournamentId);
    List<Tournament> getAllTournaments();
    List<Tournament> getAllRegisterableTournaments();
    Tournament startOrCancelTournament(Long tournamentId);
    Map<Long, Integer> getTournamentRankings(Long tournamentId);

    // Player Management
    Tournament registerPlayer(Long playerId, Long tournamentId);
    Tournament removePlayer(Long playerId, Long tournamentId);
    boolean isUserAllowedToDeletePlayer(Long playerId, String authenticatedUsername);
    Set<Player> getRegisteredPlayers(Long tournamentId);

//match management
    void scheduleMatches(Long tournamentId);
    List<ArrayList<String>> getTournamentMatchHistory(Long tournamentId);
    boolean addTestMatchToTournament(Long tournamentId, Match match);
    void sendMatchNotification(Long tournamentId, List<Match> matches);
        //uses sendNotification inside MatchService

    // Tournament Settings
    Tournament setTournamentEloRange(Long tournamentId, int minElo, int maxElo);
    Tournament setTournamentStatus(Long tournamentId, String status);
    Tournament setTournamentStyle(Long tournamentId, String style);
    Tournament setTournamentPlayerRange(Long tournamentId, int minPlayers, int maxPlayers);
    Tournament setTournamentRegistrationCutOff(Long tournamentId, LocalDateTime registrationCutOff);
    Tournament setAdmin(Long tournamentId, Admin newAdmin);
    Tournament setName(Long tournamentId, String newName);

    // Conversion Methods
    TournamentDTO convertToDTO(Tournament tournament);
    Tournament convertToEntity(TournamentDTO tournamentDTO);
}
