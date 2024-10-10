package com.g5.cs203proj.service;

import com.g5.cs203proj.exception.*;
import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerRepository playerRepository;

    //Contructors
    public TournamentServiceImpl(){};
    public TournamentServiceImpl(TournamentRepository tournamentRepository, PlayerRepository playerRepository){
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
    }


    // Tournament cycle methods
    @Override
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament updateTournament(Long tournamentId, Tournament updatedTournament) {
        Tournament existingTournament = getTournamentById(tournamentId);
        existingTournament.setName(updatedTournament.getName());
        existingTournament.setTournamentStatus(updatedTournament.getTournamentStatus());
        existingTournament.setTournamentStyle(updatedTournament.getTournamentStyle());
        existingTournament.setMaxPlayers(updatedTournament.getMaxPlayers());
        existingTournament.setMinPlayers(updatedTournament.getMinPlayers());
        existingTournament.setMinElo(updatedTournament.getMinElo());
        existingTournament.setMaxElo(updatedTournament.getMaxElo());
        existingTournament.setRegistrationCutOff(updatedTournament.getRegistrationCutOff());
        return tournamentRepository.save(existingTournament);
    }

    @Override
    public Tournament deleteTournament(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        tournamentRepository.delete(tournament);
        return tournament;
    }

    @Override
    public Tournament getTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new TournamentNotFoundException(tournamentId));
    }

    @Override
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    public List<Tournament> getAllRegisterableTournaments() {
        return tournamentRepository.findByTournamentStatus("Registration");
    }

    @Override
    public Tournament startOrCancelTournament(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        if (tournament.getRegisteredPlayers().size() >= tournament.getMinPlayers()) {
            tournament.setTournamentStatus("In Progress");
        } else {
            tournament.setTournamentStatus("Cancelled");
        }
        return tournamentRepository.save(tournament);
    }

    @Override
    public Map<Long, Integer> getTournamentRankings(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        return tournament.getRankings();
    }

    // Player management

// check if it is okay to throw exceptions here... 
    @Override
    public Tournament registerPlayer(Long playerId, Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null){
            throw new PlayerNotFoundException(playerId);
        }

        if (tournament.getRegisteredPlayers().size() >= tournament.getMaxPlayers()) {
            throw new TournamentFullException(tournamentId);
        }
        
        // if the tournament is not in the "REGISTRATION" status
        if ( !getAllRegisterableTournaments().contains(tournament) ){
            throw new TournamentNotRegisterableException("" + tournament.getName());  
        }

        // if player already signed up for the tournament 
        if (player.getTournamentRegistered().contains(tournament)){
            throw new TournamentAlreadyRegisteredException("You have already registered for " + tournament.getName());
        }

        // Add the tournament to the player's registered tournaments
        player.getTournamentRegistered().add(tournament);

        // Add the player to the tournament's list of participants (if needed)
        tournament.getRegisteredPlayers().add(player);

        // Save both player and tournament
        playerRepository.save(player);
        return tournamentRepository.save(tournament);

    }

    @Override
    public Tournament removePlayer(Long playerId, Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null){
            throw new PlayerNotFoundException(playerId);
        }
        if (tournament.getRegisteredPlayers().remove(player)){
            return tournamentRepository.save(tournament);
        }
        throw new PlayerNotInTournamentException(playerId, tournamentId);
    }

    @Override
    public List<Player> getRegisteredPlayers(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        return tournament.getRegisteredPlayers();
    }

    // Match management

    @Override
    public void scheduleMatches(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        List<Player> players = tournament.getRegisteredPlayers();
        //not sure how to implement
    }

    @Override
    public List<Match> getTournamentMatchHistory(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        return tournament.getTournamentMatchHistory();
    }

    @Override
    public void sendMatchNotification(Long tournamentId, List<Match> matches) {
        /*
        for (Match match : matches){
            match.matchService.sendNotification();
        }
        */
    }

    // Tournament settings methods

    @Override
    public Tournament setTournamentEloRange(Long tournamentId, int minElo, int maxElo) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setMinElo(minElo);
        tournament.setMaxElo(maxElo);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentStatus(Long tournamentId, String status) {
        //need check status validity?
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setTournamentStatus(status);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentStyle(Long tournamentId, String style) {
        //need check style validity?
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setTournamentStyle(style);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentPlayerRange(Long tournamentId, int minPlayers, int maxPlayers) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setMinPlayers(minPlayers);
        tournament.setMaxPlayers(maxPlayers);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setTournamentRegistrationCutOff(Long tournamentId, LocalDateTime registrationCutOff) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setRegistrationCutOff(registrationCutOff);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setAdmin(Long tournamentId, Admin newAdmin) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setAdmin(newAdmin);
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament setName(Long tournamentId, String newTournamentName) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setName(newTournamentName);
        return tournamentRepository.save(tournament);
    }


    public TournamentDTO convertToDTO(Tournament tournament) {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTournamentId(tournament.getId());
        tournamentDTO.setName(tournament.getName());
        tournamentDTO.setTournamentStatus(tournament.getTournamentStatus());
        tournamentDTO.setTournamentStyle(tournament.getTournamentStyle());
        tournamentDTO.setMaxPlayers(tournament.getMaxPlayers());
        tournamentDTO.setMinPlayers(tournament.getMinPlayers());
        tournamentDTO.setMaxElo(tournament.getMaxElo());
        tournamentDTO.setMinElo(tournament.getMinElo());
        tournamentDTO.setRegistrationCutOff(tournament.getRegistrationCutOff());

        // Collect the IDs of matches instead of including the full match objects
        List<Long> matchIdsHistory = tournament.getTournamentMatchHistory()
                                           .stream()
                                           .map(Match::getMatchId)  
                                           .collect(Collectors.toList());
        tournamentDTO.setTournamentMatchHistoryId(matchIdsHistory);

        // Convert registered players to their IDs
        List<Long> registeredPlayersIds = tournament.getRegisteredPlayers()
                                                    .stream()
                                                    .map(Player::getId)  
                                                    .collect(Collectors.toList());
        tournamentDTO.setRegisteredPlayersId(registeredPlayersIds);
        return tournamentDTO;
    }


    public Tournament convertToEntity(TournamentDTO tournamentDTO) {
        Tournament tournament = new Tournament();

        // Set the basic properties
// tournament.setId(tournamentDTO.getTournamentId());
        tournament.setName(tournamentDTO.getName());
        tournament.setTournamentStatus(tournamentDTO.getTournamentStatus());
        tournament.setTournamentStyle(tournamentDTO.getTournamentStyle());
        tournament.setMaxPlayers(tournamentDTO.getMaxPlayers());
        tournament.setMinPlayers(tournamentDTO.getMinPlayers());
        tournament.setMinElo(tournamentDTO.getMinElo());
        tournament.setMaxElo(tournamentDTO.getMaxElo());
        tournament.setRegistrationCutOff(tournamentDTO.getRegistrationCutOff());

        // Handle registeredPlayersIds (could be null or missing in the request)
        if (tournamentDTO.getRegisteredPlayersId() != null) {
            List<Player> registeredPlayers = tournamentDTO.getRegisteredPlayersId()
                    .stream()
                    .map(playerId -> playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId)))
                    .collect(Collectors.toList());
            tournament.setRegisteredPlayers(registeredPlayers);
        } else {
            tournament.setRegisteredPlayers(new ArrayList<>());  // Initialize as empty list if not provided
        }

        // Set tournament match history to an empty list initially, since matches will be added later
        tournament.setTournamentMatchHistory(new ArrayList<>());

        return tournament;
    }



}
