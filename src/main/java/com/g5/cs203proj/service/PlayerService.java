package com.g5.cs203proj.service;

import java.util.*;

public interface PlayerService {

    // Player management 
    List<Player> getAllPlayers();
    Player getPlayerById(Long id);

    Player registerPlayer(Player player);
    Player updatePlayer(Long id, Player updatedPlayer);
    Player deletePlayer(Long id);

    int getPlayerTournamentRankings(Player player, Tournament tournament); // see parameters  / consider a Map<Player, Integer>

    double getPlayerGlobalEloRating(Player player);
    
    // Player Authentication and Availability
    boolean authenticatePlayer(String username, String hashedPassword);
    
    // Participation
    Queue<Match> getPlayerMatchHistory(Player player);
    List<Tournament> getTournamentRegistered(Player player);
    List<Tournament> getActiveTournamentRegistered(Player player);
    
    
}