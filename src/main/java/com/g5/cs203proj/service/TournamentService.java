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
    Tournament updateTournament(Tournament tournament, Tournament updatedTournament);
    Tournament deleteTournament(Tournament tournament);
    Tournament getTournamentById(Long  tournamentId);
    List<Tournament> getAllTournaments();
    List<Tournament> getAllRegisterableTournaments();
        //allow filtering by other parameters?
    Tournament startOrCancelTournament(Tournament tournament); 
        //decides to start or cancel tournament at registration cut off, comparing player count and minPlayers
    Map<Long, Integer> getTournamentRankings(Tournament tournament); 
        //consider changing to Map<Integer, Set<Player>>
    

//player management
    Tournament registerPlayer(Player player, Tournament tournament);
    Tournament removePlayer(Player player, Tournament tournament);
    List<Player> getRegisteredPlayers(Tournament tournament);


//match management
    void scheduleMatches(Tournament tournament);
    List<Match> getTournamentMatchHistory(Tournament tournament);
    void sendMatchNotification(Tournament tournament, List<Match> matches);
        //uses sendNotification inside MatchService


//tournament settings
    Tournament setTournamentEloRange(Tournament tournament, int minElo, int maxElo);
    Tournament setTournamentStatus(Tournament tournament, String status);
    Tournament setTournamentStyle(Tournament tournament, String style);
    Tournament setTournamentPlayerRange(Tournament tournament, int minPlayers, int maxPlayers); 
        //restrict max players to a number that supports tournament style   
    Tournament setTournamentRegistrationCutOff(Tournament tournament, LocalDateTime registrationCutOff);
    Tournament setAdmin(Tournament tournament, Admin newAdmin);
    Tournament setName(Tournament tournament, String newTournamentName);

}
