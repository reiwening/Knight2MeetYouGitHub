package com.g5.cs203proj.service;

import java.time.LocalDateTime;
import java.util.*;


import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Ranking;
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
    List<Ranking> getTournamentRankings(Long tournamentId);
    void updateTournamentRankings(Tournament tournament, Match match);

    // Player Management
    Tournament registerPlayer(Long playerId, Long tournamentId);
    Tournament removePlayer(Long playerId, Long tournamentId);
    boolean isUserAllowedToDeletePlayer(Long playerId, String authenticatedUsername);
    Set<Player> getRegisteredPlayers(Long tournamentId);

//match management
    List<ArrayList<String>> getTournamentMatchHistory(Long tournamentId);
    boolean addTestMatchToTournament(Long tournamentId, Match match);
    List<Match> processSingleEliminationRound(Long tournamentId);
    List<Player> getWinnersForCurrentRound(Long tournamentId, int roundNumber); 

    // Tournament Settings
    Tournament setTournamentEloRange(Long tournamentId, int minElo, int maxElo);
    Tournament setTournamentStatus(Long tournamentId, String status);
    Tournament setTournamentStyle(Long tournamentId, String style);
    Tournament setTournamentPlayerRange(Long tournamentId, int minPlayers, int maxPlayers);
    Tournament setTournamentRegistrationCutOff(Long tournamentId, LocalDateTime registrationCutOff);
    Tournament setName(Long tournamentId, String newName);
    Tournament setRoundNumber(Long tournamentId, int round);

    // Conversion Methods
    TournamentDTO convertToDTO(Tournament tournament);
    Tournament convertToEntity(TournamentDTO tournamentDTO);
}
