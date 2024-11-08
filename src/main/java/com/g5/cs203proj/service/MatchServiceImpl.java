package com.g5.cs203proj.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.g5.cs203proj.DTO.MatchDTO;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.match.MatchNotFoundException;
import com.g5.cs203proj.repository.MatchRepository;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.repository.TournamentRepository;
import com.g5.cs203proj.service.PlayerService;
import com.g5.cs203proj.service.TournamentService;
import com.g5.cs203proj.exception.player.PlayerRangeException;
import com.g5.cs203proj.exception.tournament.TournamentNotFoundException;

@Service
public class MatchServiceImpl implements MatchService {

    private MatchRepository matchRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private EmailService emailService;

    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Match saveMatch(Match match) {
        return matchRepository.save(match);
    }

    @Override
    public void deleteMatch(Long id) {
        matchRepository.delete(findMatchById(id));
    }

    @Override
    public Match findMatchById(Long id) {
        return matchRepository.findById(id).orElse(null);
    }

    @Override
    public Match assignRandomPlayers(Long matchId) {
        // retrieve the match
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException(matchId));

        if (match.getTournament() == null) {
            throw new IllegalArgumentException("Match must be associated with a Tournament.");
        }

        Long tournamentIdOfMatch = match.getTournament().getId();

        // get the list of all available players 
        List<Player> availablePlayers = playerService.getAvailablePlayersForTournament(tournamentIdOfMatch);
        int playerCount = availablePlayers.size();
        if (playerCount < 2) {
            throw new PlayerRangeException(PlayerRangeException.RangeErrorType.NOT_ENOUGH_PLAYERS, "Current player count is " + playerCount);
        }
        Collections.shuffle(availablePlayers);
        Player p1 = availablePlayers.get(0);
        Player p2 = availablePlayers.get(1);
        match.setPlayer1(p1);
        match.setPlayer2(p2);
        matchRepository.save(match);

        // Add players to match histories
        p1.addMatchesAsPlayer1(match);
        p2.addMatchesAsPlayer2(match);
        playerService.savePlayer(p1);
        playerService.savePlayer(p2);

        // Send email notifications to both players
        try {
            emailService.sendMatchNotification(match);
        } catch (Exception e) {
            // Log the error but don't stop the match creation process
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        return match;
    }

    @Override
    public List<Match> createRoundRobinMatches(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new TournamentNotFoundException(tournamentId));
        
        List<Player> players = playerService.getAvailablePlayersForTournament(tournamentId);
        
        if (players.size() > 16) {
            throw new PlayerRangeException(PlayerRangeException.RangeErrorType.TOO_MANY_PLAYERS, 
                "The tournament currently has " + players.size() + " players. The maximum allowed for a round-robin format is 16.");
        }

        List<Match> matches = new ArrayList<>();
        int totalPlayers = players.size();

        for (int i = 0; i < totalPlayers; i++) {
            for (int j = i + 1; j < totalPlayers; j++) {
                Match match = new Match();
                match.setPlayer1(players.get(i));
                match.setPlayer2(players.get(j));
                match.setTournament(tournament);
                matches.add(match);
                Match savedMatch = matchRepository.save(match);

                // Send email notifications for each match
                try {
                    emailService.sendMatchNotification(savedMatch);
                } catch (Exception e) {
                    System.err.println("Failed to send email notification for match: " + savedMatch.getMatchId() + " - " + e.getMessage());
                }
            }
        }

        tournament.getTournamentMatchHistory().addAll(matches);
        tournamentRepository.save(tournament);

        return matches;
    }

    @Override
    public void processMatchResult(Match match, Player winner, boolean isDraw) {
        match.setMatchStatus("COMPLETED");
        match.setDraw(isDraw);

        if (isDraw) {
            match.setWinner(null);
        } else {
            match.setWinner(winner);
        }
        
        match.setEloChange(winner);
    }

    @Override
    public List<Match> getMatchesForTournament(Tournament tournament) {
        return null;
    }

    @Override
    public List<Match> getMatchesForPlayer(Player player) {
        return null;
    }

    @Override
    public boolean sendMatchStartNotification() {
        return false;
    }

    @Override
    public boolean bothPlayersCheckedIn(Match match) {
        return match.getStatusP1() && match.getStatusP2();
    }

    public MatchDTO convertToDTO(Match match) {
        MatchDTO matchDTO = new MatchDTO();

        matchDTO.setId(match.getMatchId());
        matchDTO.setPlayer1Id(match.getPlayer1() != null ? match.getPlayer1().getId() : null);
        matchDTO.setPlayer2Id(match.getPlayer2() != null ? match.getPlayer2().getId() : null);
        matchDTO.setTournamentId(match.getTournament().getId());
        matchDTO.setStatusP1(match.getStatusP1());
        matchDTO.setStatusP2(match.getStatusP2());
        matchDTO.setWinnerId(match.getWinner() != null ? match.getWinner().getId() : null);
        matchDTO.setDraw(match.getDraw());
        matchDTO.setMatchStatus(match.getMatchStatus());
        matchDTO.setEloChange(match.getEloChange());

        return matchDTO;
    }

    public Match convertToEntity(MatchDTO matchDTO) {
        Match match = new Match();

        Player player1 = playerService.getPlayerById(matchDTO.getPlayer1Id());
        Player player2 = playerService.getPlayerById(matchDTO.getPlayer2Id());
        Tournament tournament = tournamentService.getTournamentById(matchDTO.getTournamentId());
    
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setTournament(tournament);
        match.setStatusP1(matchDTO.isStatusP1());
        match.setStatusP2(matchDTO.isStatusP2());
        match.setWinner(matchDTO.getWinnerId() != null ? playerService.getPlayerById(matchDTO.getWinnerId()) : null);
        match.setDraw(matchDTO.isDraw());
        match.setMatchStatus(matchDTO.getMatchStatus());    
        match.setOnlyEloChange(matchDTO.getEloChange());
    
        return match;
    }
}
