package com.g5.cs203proj.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.g5.cs203proj.entity.Admin;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;

public interface TournamentService {
//Tournament cycle
    Tournament createTournament(Tournament tournament);
    Tournament updateTournament(Long tournamentId, Tournament updatedTournament);
    Tournament deleteTournament(Long tournamentId);
    Tournament getTournamentById(Long tournamentId);
    List<Tournament> getAllTournaments();
    List<Tournament> getAllRegisterableTournaments();
        //allow filtering by other parameters?
    Tournament startOrCancelTournament(Long tournamentId); 
        //decides to start or cancel tournament at registration cut off, comparing player count and minPlayers
    Map<Long, Integer> getTournamentRankings(Long tournamentId); 
        //consider changing to Map<Integer, Set<Player>>
    

//player management
    Tournament registerPlayer(Long playerId, Long tournamentId);
    Tournament removePlayer(Long playerId, Long tournamentId);
    List<Player> getRegisteredPlayers(Long tournamentId);


//match management
    void scheduleMatches(Long tournamentId);
    List<Match> getTournamentMatchHistory(Long tournamentId);
    void sendMatchNotification(Long tournamentId, List<Match> matches);
        //uses sendNotification inside MatchService


//tournament settings
    Tournament setTournamentEloRange(Long tournamentId, int minElo, int maxElo);
    Tournament setTournamentStatus(Long tournamentId, String status);
    Tournament setTournamentStyle(Long tournamentId, String style);
    Tournament setTournamentPlayerRange(Long tournamentId, int minPlayers, int maxPlayers); 
        //restrict max players to a number that supports tournament style   
    Tournament setTournamentRegistrationCutOff(Long tournamentId, LocalDateTime registrationCutOff);
    Tournament setAdmin(Long tournamentId, Admin newAdmin);
    Tournament setName(Long tournamentId, String newTournamentName);

}
