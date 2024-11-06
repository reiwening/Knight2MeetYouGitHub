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
import com.g5.cs203proj.DTO.PlayerDTO;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.service.TournamentService;
import com.g5.cs203proj.service.MatchService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private PlayerRepository playerRepository;
    private TournamentService tournamentService;
    private MatchService matchService;
    private BCryptPasswordEncoder bCryptPasswordEncoder; // this is a service layer to handle password encoding before
                                                         // storing the password
                                                         // provided in the `SecurityConfig` Class

    // constructor
    public PlayerServiceImpl(PlayerRepository playerRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
            TournamentService tournamentService) {
        this.playerRepository = playerRepository;
        this.tournamentService = tournamentService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // override the methods for PlayerService interface

    @Override
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Optional<Player> findPlayerByUsername(String username) {
        return playerRepository.findByUsername(username); // Repository method to find player by username

    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    private boolean hasRole(Player player, String role) {
        return player.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    @Override
    public List<Player> getAllAdmins() {
        List<Player> allPlayers = getAllPlayers();
        List<Player> admins = allPlayers.stream()
                .filter(player -> hasRole(player, "ROLE_ADMIN"))
                .collect(Collectors.toList());
        return admins;
    }

    @Override
    public List<Player> getAllPlayerUsers() {
        List<Player> allPlayers = getAllPlayers();
        List<Player> users = allPlayers.stream()
                .filter(player -> hasRole(player, "ROLE_USER"))
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
        playerRepository.save(player); // Save the player with the updated Elo rating
    }

    @Override
    public double getPlayerGlobalEloRating(Player player) {
        return player.getGlobalEloRating();
    }

    @Override
    public List<Match> getPlayerMatchHistory(Player player) {
        return player.getMatchHistory();
    }

    public List<Player> getAvailablePlayersForTournament(Long tournamentIdOfMatch){
        return playerRepository.findAllByTournamentIdAndNotInOngoingMatch(tournamentIdOfMatch);
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


    public PlayerDTO convertToPlayerDTO(Player player) {
        Set<Long> tournamentIds = player.getTournamentRegistered()
            .stream()
            .map(Tournament::getId)
            .collect(Collectors.toSet());

        List<Long> matchIds = player.getMatchHistory()
            .stream()
            .map(Match::getMatchId)
            .collect(Collectors.toList());

        // Include the authorities field in the DTO mapping
        return new PlayerDTO(
            player.getId(),
            player.getUsername(),
            null, // make this for use to see non-null , password shdnt be seen too
            player.getGlobalEloRating(),
            tournamentIds,
            matchIds,
            player.getAuthorities().iterator().next().getAuthority() // Getting the authorities string
        ); 
    }



    public Player convertToEntity(PlayerDTO playerDTO) {
        Player player = new Player();
    
        // player.setId(playerDTO.getId());
        player.setUsername(playerDTO.getUsername());
        player.setGlobalEloRating(playerDTO.getGlobalEloRating());
        player.setPassword(playerDTO.getPassword()); // Set the raw password, will be hashed in registerPlayer method

    
        // Set authorities (e.g., ROLE_USER or ROLE_ADMIN)
        player.setAuthorities(playerDTO.getAuthorities());

        // get Match History based on match history ids 
        List<Long> matchIds = playerDTO.getMatchHistoryIds();
        List<Match> matchHistory = matchIds.stream()
                                            .map(id -> matchService.findMatchById(id))
                                            .filter(Objects::nonNull)  // Remove nulls in case of missing matches
                                            .collect(Collectors.toList());
        player.setMatchHistory(matchHistory);


        // get TournamentReg 
        Set<Long> tournamentIds = playerDTO.getTournamentRegisteredIds();
        Set<Tournament> tournaments = tournamentIds.stream()
            .map(id -> tournamentService.getTournamentById(id))
            .filter(Objects::nonNull)  // Remove nulls in case of missing tournaments
            .collect(Collectors.toSet());
        player.setTournamentRegistered(tournaments);
        

        return player;
    }
    



    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public Set<Tournament> getTournamentRegistered(Player player) {
        return player.getTournamentRegistered();
    }

    @Override
    public Player updatePlayer(Long id, Player updatedPlayer) {
        return null;
    }

    @Override
    public void deletePlayer(String username) {
        Optional<Player> p = findPlayerByUsername(username);
        if (!p.isPresent()) {
            throw new PlayerNotFoundException(username);
        }
        playerRepository.delete(p.get());
    }

    @Override
    public int getPlayerTournamentRankings(Player player, Tournament tournament) {
        return 0;
    }

    @Override
    public List<Tournament> getActiveTournamentRegistered(Player player) {
        return null;
    }


    
}
