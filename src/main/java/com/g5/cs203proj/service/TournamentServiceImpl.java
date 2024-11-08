package com.g5.cs203proj.service;

import com.g5.cs203proj.enums.*;
import com.g5.cs203proj.exception.*;
import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.repository.*;

import jakarta.validation.OverridesAttribute;

import org.hibernate.mapping.Array;
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
    @Autowired
    private MatchRepository matchRepository;

//Contructors
    public TournamentServiceImpl(){};
    public TournamentServiceImpl(TournamentRepository tournamentRepository, PlayerRepository playerRepository, MatchRepository matchRepository){
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }


// Tournament cycle methods
    @Override
    public Tournament createTournament(Tournament tournament) {
        //field validation
        eloRangeValidation(tournament, tournament.getMinElo(), tournament.getMaxElo());
        playerRangeValidation(tournament, tournament.getMinPlayers(), tournament.getMaxPlayers());
        String style = tournament.getTournamentStyle().toUpperCase();
        styleValidation(style);
        String status = tournament.getTournamentStatus().toUpperCase();
        statusValidation(status);
        //safe to create
        //make sure casing is correct
        tournament.setTournamentStatus(status);
        tournament.setTournamentStyle(style);
        return tournamentRepository.save(tournament);
    }

    // Update a tournament
    @Override
    public Tournament updateTournament(Long tournamentId, Tournament updatedTournament) {
        Tournament existingTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        String status = updatedTournament.getTournamentStatus().toUpperCase();
        String style = updatedTournament.getTournamentStyle().toUpperCase();
        int minPlayers = updatedTournament.getMinPlayers();
        int maxPlayers = updatedTournament.getMaxPlayers();
        int minElo = updatedTournament.getMinElo();
        int maxElo = updatedTournament.getMaxElo();

        //field validation
        eloRangeValidation(existingTournament, minElo, maxElo);
        playerRangeValidation(existingTournament, minPlayers, maxPlayers);
        styleValidation(style);
        statusValidation(status);

        //safe to update
        existingTournament.setName(updatedTournament.getName());
        existingTournament.setTournamentStatus(status);
        existingTournament.setTournamentStyle(style);
        existingTournament.setMaxPlayers(maxPlayers);
        existingTournament.setMinPlayers(minPlayers);
        existingTournament.setMinElo(minElo);
        existingTournament.setMaxElo(maxElo);
        existingTournament.setRegistrationCutOff(updatedTournament.getRegistrationCutOff());

        // Update registered players if needed
        if (updatedTournament.getRegisteredPlayers() != null) {
            playerRangeValidation(updatedTournament, minPlayers, maxPlayers);
            existingTournament.setRegisteredPlayers(updatedTournament.getRegisteredPlayers());
        }

        // Update match history if needed
        if (updatedTournament.getTournamentMatchHistory() != null) {
            existingTournament.setTournamentMatchHistory(updatedTournament.getTournamentMatchHistory());
        }

        return tournamentRepository.save(existingTournament);
    }

    // Delete a tournament
    @Override
    public void deleteTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));
        tournamentRepository.delete(tournament);
    }

    // Get a tournament by ID
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
        return tournamentRepository.findByTournamentStatus("registration");
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

    // Get tournament rankings
    @Override
    public Map<Long, Integer> getTournamentRankings(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        return tournament.getRankings();
    }

    // Player management
    // Register a player to a tournament
    @Override
    public Tournament registerPlayer(Long playerId, Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));


        if (tournament.getRegisteredPlayers().contains(player)) {
            throw new PlayerAlreadyInTournamentException(playerId, tournamentId);
        }

        if (tournament.getRegisteredPlayers().size() >= tournament.getMaxPlayers()) {
            throw new TournamentFullException(tournamentId);
        }

        tournament.getRegisteredPlayers().add(player);
        return tournamentRepository.save(tournament);
    }

    // Remove a player from a tournament
    @Override
    public Tournament removePlayer(Long playerId, Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        if (!tournament.getRegisteredPlayers().contains(player)) {
            throw new PlayerNotInTournamentException(playerId, tournamentId);
        }

        tournament.getRegisteredPlayers().remove(player);
        return tournamentRepository.save(tournament);
    }

    @Override
    public boolean isUserAllowedToDeletePlayer(Long playerId, String authenticatedUsername) {
        // Logic to check if the username matches the playerId
        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        return player.getUsername().equals(authenticatedUsername); // assuming the Player entity has a username field
    }

    @Override
    public Set<Player> getRegisteredPlayers(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        return tournament.getRegisteredPlayers();
    }

// Match management

    @Override
    public void scheduleMatches(Long tournamentId) {
        //not sure how to implement
    }

    @Override
    public List<ArrayList<String>> getTournamentMatchHistory(Long tournamentId) {
        // Extract matches for a tournament
        Tournament tournament = getTournamentById(tournamentId);
        List<Match> matches = tournament.getTournamentMatchHistory();
        
        // Add each match's info to a list of all matches' info
        List<ArrayList<String>> detailedMatchInfo = new ArrayList<ArrayList<String>>();
        ArrayList<String> matchInfo = new ArrayList<String>();
        for (Match m : matches) {
            // Combine each match's info into 1 ArrayList
            String matchId = "" + m.getMatchId();

            Player p1 = m.getPlayer1();
            String p1Name = null;
            if (p1 != null) {
                p1Name = p1.getUsername();
            }

            Player p2 = m.getPlayer2();
            String p2Name = null;
            if (p2 != null) {
                p2Name = p2.getUsername();
            }

            Player winner = m.getWinner();
            String winnerName = null;
            if (winner != null) {
                winnerName = winner.getUsername();
            }

            String eloChange = "" + m.getEloChange();
            String isDraw = "" + m.getDraw();

            matchInfo.add(matchId);
            matchInfo.add(p1Name);
            matchInfo.add(p2Name);
            matchInfo.add(winnerName);
            matchInfo.add(eloChange);
            matchInfo.add(isDraw);
            detailedMatchInfo.add(matchInfo);
        }

        return detailedMatchInfo;
    }

    @Override
    public boolean addTestMatchToTournament(Long tournamentId, Match match) {
        Tournament t = getTournamentById(tournamentId);
        
        // attach this tournament to the match added
        match.setTournament(t);
        matchRepository.save(match);

        // add match to tournament matches
        t.addTestMatch(match);
        tournamentRepository.save(t);
        return true;
    }

    //if we store the matches in the most recent round, we can just iterate through that instead of having to pass in matches
    public void sendMatchNotification(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        /*
        for (Match match : matches){
            match.matchService.sendNotification();
        }
        */
        
    }

// Tournament settings methods
    // Set Elo Range
    @Override
    public Tournament setTournamentEloRange(Long tournamentId, int minElo, int maxElo) {
        Tournament tournament = getTournamentById(tournamentId);
        //validate input
        eloRangeValidation(tournament, minElo, maxElo);
        //no exception, safe to add
        tournament.setMinElo(minElo);
        tournament.setMaxElo(maxElo);
        return tournamentRepository.save(tournament);
    }

    // Set Tournament Status
    @Override
    public Tournament setTournamentStatus(Long tournamentId, String status) {
        Tournament tournament = getTournamentById(tournamentId);
        // Validate status if necessary
        statusValidation(status);
        //nv throw exception, safe to add
        tournament.setTournamentStatus(status.toUpperCase());
        return tournamentRepository.save(tournament);
    }

    // Set Tournament Style
    @Override
    public Tournament setTournamentStyle(Long tournamentId, String style) {
        Tournament tournament = getTournamentById(tournamentId);
        // Validate style
        styleValidation(style);
        //no exception, safe to add
        tournament.setTournamentStyle(style.toUpperCase());
        return tournamentRepository.save(tournament);
    }

    // Set Player Range
    @Override
    public Tournament setTournamentPlayerRange(Long tournamentId, int minPlayers, int maxPlayers) {
        Tournament tournament = getTournamentById(tournamentId);
        //validation
        playerRangeValidation(tournament, minPlayers, maxPlayers);
        //nv throw exception, safe to add
        tournament.setMinPlayers(minPlayers);
        tournament.setMaxPlayers(maxPlayers);
        return tournamentRepository.save(tournament);
    }

    // Set Registration Cutoff
    @Override
    public Tournament setTournamentRegistrationCutOff(Long tournamentId, LocalDateTime registrationCutOff) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setRegistrationCutOff(registrationCutOff);
        return tournamentRepository.save(tournament);
    }

    // Set Admin
    @Override
    public Tournament setAdmin(Long tournamentId, Admin newAdmin) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setAdmin(newAdmin);
        return tournamentRepository.save(tournament);
    }

    // Set Name
    @Override
    public Tournament setName(Long tournamentId, String newName) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setName(newName);
        return tournamentRepository.save(tournament);
    }

// Convert Entity to DTO
    @Override
    public TournamentDTO convertToDTO(Tournament tournament) {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTournamentId(tournament.getId());
        tournamentDTO.setName(tournament.getName());
        tournamentDTO.setTournamentStatus(tournament.getTournamentStatus());
        tournamentDTO.setTournamentStyle(tournament.getTournamentStyle());
        tournamentDTO.setMaxPlayers(tournament.getMaxPlayers());
        tournamentDTO.setMinPlayers(tournament.getMinPlayers());
        tournamentDTO.setMinElo(tournament.getMinElo());
        tournamentDTO.setMaxElo(tournament.getMaxElo());
        tournamentDTO.setRegistrationCutOff(tournament.getRegistrationCutOff());

        // Collect match IDs
        List<Long> matchIdsHistory = tournament.getTournamentMatchHistory().stream()
                .map(Match::getMatchId)
                .collect(Collectors.toList());
        tournamentDTO.setTournamentMatchHistoryId(matchIdsHistory);

        // Collect registered player IDs
        List<Long> registeredPlayersIds = tournament.getRegisteredPlayers().stream()
                .map(Player::getId)
                .collect(Collectors.toList());
        tournamentDTO.setRegisteredPlayersId(registeredPlayersIds);

        return tournamentDTO;
    }

// Convert DTO to Entity
    @Override
    public Tournament convertToEntity(TournamentDTO tournamentDTO) {
        Tournament tournament = new Tournament();
        tournament.setName(tournamentDTO.getName());
        tournament.setTournamentStatus(tournamentDTO.getTournamentStatus());
        tournament.setTournamentStyle(tournamentDTO.getTournamentStyle());
        tournament.setMaxPlayers(tournamentDTO.getMaxPlayers());
        tournament.setMinPlayers(tournamentDTO.getMinPlayers());
        tournament.setMinElo(tournamentDTO.getMinElo());
        tournament.setMaxElo(tournamentDTO.getMaxElo());
        tournament.setRegistrationCutOff(tournamentDTO.getRegistrationCutOff());

        // Handle registeredPlayersIds
        if (tournamentDTO.getRegisteredPlayersId() != null) {
            Set<Player> registeredPlayers = tournamentDTO.getRegisteredPlayersId().stream()
                    .map(playerId -> playerRepository.findById(playerId)
                            .orElseThrow(() -> new PlayerNotFoundException(playerId)))
                    .collect(Collectors.toSet());
            tournament.setRegisteredPlayers(registeredPlayers);
        } else {
            tournament.setRegisteredPlayers(new HashSet<>());
        }

        // Handle tournamentMatchHistoryIds
        if (tournamentDTO.getTournamentMatchHistoryId() != null) {
            List<Match> tournamentMatchHistory = tournamentDTO.getTournamentMatchHistoryId().stream()
                    .map(matchId -> matchRepository.findById(matchId)
                            .orElseThrow(() -> new MatchNotFoundException(matchId)))
                    .collect(Collectors.toList());
            tournament.setTournamentMatchHistory(tournamentMatchHistory);
        } else {
            tournament.setTournamentMatchHistory(new ArrayList<>());
        }

        return tournament;
    }

//field validation methods
    private void playerRangeValidation(Tournament tournament, int minPlayers, int maxPlayers){
        if (minPlayers < 0 || maxPlayers < 0) {
            throw new InvalidPlayerRangeException("Player count cannot be negative");
        }
        if (minPlayers > maxPlayers) {
            throw new InvalidPlayerRangeException("minPlayers cannot be greater than maxPlayers");
        }
        int playerCount = tournament.getRegisteredPlayers().size();
        if (playerCount > maxPlayers) {
            throw new InvalidPlayerRangeException(String.format("Tournament has more players(%d) than new maxPlayers(%d)", playerCount, maxPlayers));
        }
    }

    private void styleValidation(String style){
        if (!Styles.isValidStyle(style)) {
            throw new InvalidStyleException("Invalid tournament style: " + style);
        }
    }

    private void statusValidation(String status){
        if (!Statuses.isValidStatus(status)) {
            throw new InvalidStatusException("Invalid tournament status: " + status);
        }
    }

    private void eloRangeValidation(Tournament tournament, int minElo, int maxElo){
        if (minElo < 0 || maxElo < 0) {
            throw new InvalidEloValueException("Elo values cannot be negative");
        }
        if (minElo > maxElo) {
            throw new InvalidEloValueException("minElo cannot be greater than maxElo");
        }
        //check if existing players adhere to the new Elo range
        boolean playersWithinRange = tournament.getRegisteredPlayers().stream()
                .allMatch(player -> player.getGlobalEloRating() >= minElo && player.getGlobalEloRating() <= maxElo);

        if (!playersWithinRange) {
            throw new InvalidEloValueException("Not all players meet the new Elo range criteria");
        }
    }
}
