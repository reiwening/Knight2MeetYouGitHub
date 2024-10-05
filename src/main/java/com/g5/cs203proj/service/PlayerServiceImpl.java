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
import java.util.stream.Collectors;


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

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }


    private boolean hasRole(Player player, String role){
        return player.getAuthorities().stream()
                                      .anyMatch(authority -> authority.getAuthority().equals(role));
    }
    
    @Override 
    public List<Player> getAllAdmins() {
        List<Player> allPlayers = getAllPlayers();
        List<Player> admins = allPlayers.stream()
                                        .filter( player -> hasRole(player, "ROLE_ADMIN"))
                                        .collect(Collectors.toList());
        return admins;
    }

    @Override 
    public List<Player> getAllPlayerUsers() {
        List<Player> allPlayers = getAllPlayers();
        List<Player> users = allPlayers.stream()
                                        .filter( player -> hasRole(player, "ROLE_USER"))
                                        .collect(Collectors.toList());
        return users;
    }

    @Override
    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }
    
    @Override
    public void setPlayerGlobalEloRating(Player player, double newRating) {
        player.setGlobalEloRating(newRating);
        playerRepository.save(player);  // Save the player with the updated Elo rating
    }

    @Override
    public double getPlayerGlobalEloRating(Player player) {
        return player.getGlobalEloRating();
    }
    
    @Override
    public List<Tournament> getTournamentRegistered(Player player) {
        return player.getTournamentRegistered();
    }

    @Override
    public List<Match> getPlayerMatchHistory(Player player) {
        return player.getMatchHistory();
    }

//////////////////////////////////////////////////////////////////////////////////

    @Override
    public Player updatePlayer(Long id, Player updatedPlayer) {
        return null;
    }

    @Override
    public Player deletePlayer(Long id) {
        return null;
    }

    @Override
    public int getPlayerTournamentRankings(Player player, Tournament tournament) {
        return 0;
    }

    @Override
    public List<Tournament> getActiveTournamentRegistered(Player player) {
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