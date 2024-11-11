package com.g5.cs203proj.service;

import com.g5.cs203proj.enums.*;
import com.g5.cs203proj.exception.player.*;
import com.g5.cs203proj.exception.tournament.*;
import com.g5.cs203proj.exception.inputs.InvalidEloValueException;
import com.g5.cs203proj.exception.inputs.InvalidStatusException;
import com.g5.cs203proj.exception.inputs.InvalidStyleException;
import com.g5.cs203proj.exception.match.MatchNotFoundException;
// import com.g5.cs203proj.exception.player.InvalidPlayerRangeException;
import com.g5.cs203proj.exception.player.PlayerAvailabilityException;
import com.g5.cs203proj.exception.player.PlayerRangeException;
import com.g5.cs203proj.exception.tournament.*;
import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.repository.*;

import jakarta.validation.OverridesAttribute;

import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.mapping.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.Math;



@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private EmailService emailService;

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
        int roundNumber = updatedTournament.getRoundNumber();

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
        existingTournament.setRoundNumber(roundNumber);

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
                .orElseThrow(() -> new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.NOT_FOUND));


        if (tournament.getRegisteredPlayers().contains(player)) {
            throw new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.ALREADY_IN_TOURNAMENT);
            // throw new PlayerAlreadyInTournamentException(playerId, tournamentId);
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
                .orElseThrow(() -> new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.NOT_FOUND));

        if (!tournament.getTournamentStatus().equals("REGISTRATION")) {
            throw new NotRegistrationPeriodException("Cannot leave tournament. Tournament is " + tournament.getTournamentStatus() + ".");
        }

        if (!tournament.getRegisteredPlayers().contains(player)) {
            throw new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.NOT_IN_TOURNAMENT);

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
    public void sendMatchNotification(Long tournamentId, List<Match> matches) {
        Tournament tournament = getTournamentById(tournamentId);
        /*
        for (Match match : matches){
            match.matchService.sendNotification();
        }
        */
        
    }
    
    @Override
    public List<Match> processSingleEliminationRound(Long tournamentId) {
        // Get tournament and matches from current round
        Tournament tournament = getTournamentById(tournamentId);
        List<Match> matches = tournament.getTournamentMatchHistory();

        // Get first match of next round
        int firstMatchOfNextRoundIdx = -1;
        for (int i = 0; i < matches.size(); i++) {
            Match m = matches.get(i);
            if (m.getMatchStatus().equals("NOT_STARTED")) {
                firstMatchOfNextRoundIdx = i;
                break;
            }
        }
        // System.out.println("first match of next round: " + firstMatchOfNextRoundIdx);
        // System.out.println("matches size: " + matches.size());

        if (firstMatchOfNextRoundIdx == -1) {
            throw new IllegalStateException("No NOT_STARTED matches available for the next round");
        }

        // Get current round number and winners from all matches in current round
        int roundNumber = tournament.getRoundNumber();
        List<Player> winners = getWinnersForCurrentRound(tournamentId, roundNumber);
        // System.out.println("winners size: " + winners.size());
        roundNumber = roundNumber + 1;
        tournament.setRoundNumber(roundNumber);
        tournamentRepository.save(tournament);

        // Validate enough winners to form the next round
        if (winners.size() % 2 != 0) {
            throw new IllegalStateException("Odd number of winners, unable to form pairs for the next round");
        }

        // Assign winners to matches in next round
        int playerIdx = 0;
        int playersInNextRound = winners.size();
        int matchesInNextRound = playersInNextRound / 2;

        for (int i = 0; i < matchesInNextRound; i++) {
            int matchIdx = firstMatchOfNextRoundIdx + i;
            if (matchIdx >= matches.size()) {
                throw new IndexOutOfBoundsException("Not enough matches available for pairing in the next round");
            }
            
            Match match = matches.get(matchIdx);
            match.setPlayer1(winners.get(playerIdx++));
            match.setPlayer2(winners.get(playerIdx++));
            match.setMatchStatus("NOT_STARTED"); // Ensure the match is set to NOT_STARTED
            Match savedMatch = matchRepository.save(match);

            // Send email notifications for each match
            try {
                emailService.sendMatchNotification(savedMatch);
            } catch (Exception e) {
                System.err.println("Failed to send email notification for match: " + savedMatch.getMatchId() + " - " + e.getMessage());
            }
        }
        
        // save tournament 
        tournamentRepository.save(tournament);

        return matches;
    }

    @Override
    public List<Player> getWinnersForCurrentRound(Long tournamentId, int roundNumber) {
        // Get tournament and matches from current round
        Tournament tournament = getTournamentById(tournamentId);
        List<Match> matches = tournament.getTournamentMatchHistory();

        // Determine the required number of winners for this round
        int totalPlayerSize = tournament.getRegisteredPlayers().size();
        int winnerSizeForCurrentRound = totalPlayerSize / (int) Math.pow(2, roundNumber);

        // Count wins of all players
        HashMap<Player, Long> allPlayerWins = new HashMap<>();
        for (Match m : matches) {
            Player winner = m.getWinner();
            int numWins = 0;
            if (winner != null) {
                if (allPlayerWins.containsKey(winner)) {
                    numWins = allPlayerWins.get(winner).intValue();
                    numWins++;
                    allPlayerWins.replace(winner, Long.valueOf(numWins));
                } else {
                    numWins = 1;
                    allPlayerWins.put(winner, Long.valueOf(numWins));
                }
            }
        }


        // Convert the TreeMap entries to a list and sort by value
        List<Map.Entry<Player, Long>> sortedByWins = new ArrayList<>(allPlayerWins.entrySet());
        sortedByWins.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Extract the top winners for the current round
        List<Player> winners = new ArrayList<>();
        for (int i = 0; i < Math.min(winnerSizeForCurrentRound, sortedByWins.size()); i++) {
            winners.add(sortedByWins.get(i).getKey());
        }

        return winners;
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
    // @Override
    // public Tournament setAdmin(Long tournamentId, Admin newAdmin) {
    //     Tournament tournament = getTournamentById(tournamentId);
    //     tournament.setAdmin(newAdmin);
    //     return tournamentRepository.save(tournament);
    // }

    // Set Name
    @Override
    public Tournament setName(Long tournamentId, String newName) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setName(newName);
        return tournamentRepository.save(tournament);
    }

    // set round number
    @Override
    public Tournament setRoundNumber(Long tournamentId, int round) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setRoundNumber(round);
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
        tournamentDTO.setRoundNumber(tournament.getRoundNumber());

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
        tournament.setRoundNumber(tournamentDTO.getRoundNumber());

        // Handle registeredPlayersIds
        if (tournamentDTO.getRegisteredPlayersId() != null) {
            Set<Player> registeredPlayers = tournamentDTO.getRegisteredPlayersId().stream()
                    .map(playerId -> playerRepository.findById(playerId)
                            .orElseThrow(() -> new PlayerAvailabilityException(PlayerAvailabilityException.AvailabilityType.NOT_FOUND)))
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
            throw new PlayerRangeException(PlayerRangeException.RangeErrorType.INVALID_RANGE, "Player count cannot be negative" );
            // throw new InvalidPlayerRangeException("Player count cannot be negative");
        }
        if (minPlayers > maxPlayers) {
            throw new PlayerRangeException(PlayerRangeException.RangeErrorType.INVALID_RANGE, "minPlayers cannot be greater than maxPlayers" );
            // throw new InvalidPlayerRangeException("minPlayers cannot be greater than maxPlayers");
        }
        int playerCount = tournament.getRegisteredPlayers().size();
        if (playerCount > maxPlayers) {
            throw new PlayerRangeException(PlayerRangeException.RangeErrorType.INVALID_RANGE, String.format("Tournament has more players(%d) than new maxPlayers(%d)", playerCount, maxPlayers) );
            // throw new InvalidPlayerRangeException(String.format("Tournament has more players(%d) than new maxPlayers(%d)", playerCount, maxPlayers));
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
