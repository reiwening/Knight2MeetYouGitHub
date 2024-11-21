package com.g5.cs203proj.service;

import java.util.*;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.DTO.PlayerDTO;
import com.g5.cs203proj.entity.Match;


public interface PlayerService {

    // Player management 
    List<Player> getAllPlayers();
    Player getPlayerById(Long id);
    void validateUserAccess(String username);
    Player savePlayer( Player player );
    Player updatePlayer(Long id, Player updatedPlayer);
    void deletePlayer(String username);
    Player registerPlayer(Player playerToRegister );
    List<Player> getAllAdmins() ;
    List<Player> getAllPlayerUsers();
    int getPlayerTournamentRankings(Player player, Tournament tournament); 

    double getPlayerGlobalEloRating(Player player);
    void setPlayerGlobalEloRating(Player player, double change);

    
    // Participation
    List<Match> getPlayerMatchHistory(Player player);
    Set<Tournament> getTournamentRegistered(Player player);
    List<Tournament> getActiveTournamentRegistered(Player player);

    Optional<Player> findPlayerByUsername(String username);
    List<Player> getAvailablePlayersForTournament(Long tournamentIdOfMatch);
    
    Player convertToEntity(PlayerDTO playerDTO);
    PlayerDTO convertToPlayerDTO(Player player) ;


}