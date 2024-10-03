package com.g5.cs203proj.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder; // this is a service layer to handle password encoding before storing the password 
                                                         // provided in the `SecurityConfig` Class

    // constructor 
    public PlayerServiceImpl( PlayerRepository playerRepository, BCryptPasswordEncoder bCryptPasswordEncoder ) {
        this.playerRepository = playerRepository;
        this.bCryptPasswordEncoder= bCryptPasswordEncoder;
    }

// override the methods for PlayerService interface

    @Override
    public Player savePlayer( Player player ) {
        return playerRepository.save(player);
    }

    public Player registerPlayer(Player playerToRegister ) {
        Optional<Player> existingPlayer = findPlayerByUsername(playerToRegister.getUsername()); 
        if (existingPlayer.isPresent()) {
            return null;
        } 

        playerToRegister.setPassword(bCryptPasswordEncoder.encode(playerToRegister.getPassword())); // Hash password

        /* else we have to save to the DB */
        return savePlayer(playerToRegister);

    }
    
    @Override
    public Optional<Player> findPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);  // Repository method to find player by username
    }

//////////////////////////////////////////////////////////////////////////////////
    
    @Override
    @PreAuthorize("#id == principal.id") // only the user with that ID can change his or her setting 
    public Player updatePlayer(Long id, Player updatedPlayer) {
        // Optional<Player> existingPlayer = playerRepository.findById(id);
        // if ( ! existingPlayer.isPresent() ) {
        //     return null;
        // } 
        // // whatever fields you need to update 
        // existingPlayer.setUsername(updatedPlayer.getUsername());
        // return playerRepository.save(existingPlayer);
        return null;
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
    public void setPlayerGlobalEloRating(Player player, double newRating) {
        player.setGlobalEloRating(newRating);
        playerRepository.save(player);  // Save the player with the updated Elo rating
    }
    
   
    
    
}