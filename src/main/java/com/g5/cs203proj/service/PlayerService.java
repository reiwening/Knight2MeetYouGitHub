package com.g5.cs203proj.service;

import java.util.*;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.entity.Match;


public interface PlayerService {

    // Player management 
    List<Player> getAllPlayers();
    Player getPlayerById(Long id);

    Player savePlayer( Player player );
    Player updatePlayer(Long id, Player updatedPlayer);
    Player deletePlayer(Long id);
    Player registerPlayer(Player playerToRegister );
    List<Player> getAllAdmins() ;
    List<Player> getAllPlayerUsers();
    int getPlayerTournamentRankings(Player player, Tournament tournament); // see parameters  / consider a Map<Player, Integer>

    double getPlayerGlobalEloRating(Player player);
    void setPlayerGlobalEloRating(Player player, double change);

    
    // Participation
    List<Match> getPlayerMatchHistory(Player player);
    List<Tournament> getTournamentRegistered(Player player);
    List<Tournament> getActiveTournamentRegistered(Player player);

    Optional<Player> findPlayerByUsername(String username);
    List<Player> getAvailablePlayersForTournament(Long tournamentIdOfMatch);

    // List<Match> getMatchesAsPlayer1(Player player);
    // List<Match> getMatchesAsPlayer2(Player player);
    // Match addMatchToPlayerHistory(Player player, Match match);
    // boolean registerPlayerForTournament(String username, Long tournamentId) ;
    
}