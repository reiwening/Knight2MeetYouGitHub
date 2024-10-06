package com.g5.cs203proj.service;

import org.springframework.stereotype.Service;

import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.PlayerNotFoundException;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.repository.PlayerRepository;

import java.util.*;

@Service
public class PlayerServiceImpl implements PlayerService {

    private PlayerRepository playerRepository;

    // constructor 
    public PlayerServiceImpl( PlayerRepository playerRepository ) {
        this.playerRepository = playerRepository;
    }
    
// override the methods

    @Override
    public Player savePlayer( Player player ) {
        return playerRepository.save(player);
    }


    @Override   
    public boolean authenticatePlayer(String username, String hashedPassword) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Player deletePlayer(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Tournament> getActiveTournamentRegistered(Player player) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Player> getAllPlayers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    @Override
    public double getPlayerGlobalEloRating(Player player) {
        // TODO Auto-generated method stub
        return 0;
    }

    // @Override
    // public void setPlayerGlobalEloRating(Player player, double change) {
    //     // TODO Auto-generated method stub
    //     double oldPlayerElo = player.getGlobalEloRating();
    //     return player.setGlobalEloRating(oldPlayerElo + change);
    // }

    @Override
    public Queue<Match> getPlayerMatchHistory(Player player) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPlayerTournamentRankings(Player player, Tournament tournament) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Tournament> getTournamentRegistered(Player player) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Player registerPlayer(Player player) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Player updatePlayer(Long id, Player updatedPlayer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Match> getMatchesAsPlayer1(Player player) {
        return player.getMatchesAsPlayer1();
    }

    @Override
    public List<Match> getMatchesAsPlayer2(Player player) {
        return player.getMatchesAsPlayer2();
    }

    @Override
    public Match addMatchToPlayerHistory(Player player, Match match) {
        if (match.getPlayer1() == player) {
            return player.addMatchesAsPlayer1(match);
        }
        return player.addMatchesAsPlayer2(match);
    }
    
    
}