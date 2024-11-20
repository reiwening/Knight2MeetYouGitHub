package com.g5.cs203proj.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.g5.cs203proj.DTO.MatchDTO;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.global.*;
import com.g5.cs203proj.exception.inputs.*;
import com.g5.cs203proj.exception.match.*;
import com.g5.cs203proj.exception.player.*;
import com.g5.cs203proj.exception.tournament.*;
import com.g5.cs203proj.repository.MatchRepository;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.repository.TournamentRepository;

import jakarta.validation.OverridesAttribute;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerService playerService;
    private final TournamentService tournamentService;
    private final EmailService emailService;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository, TournamentRepository tournamentRepository,
            PlayerService playerService, TournamentService tournamentService, EmailService emailService) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
        this.playerService = playerService;
        this.tournamentService = tournamentService;
        this.emailService = emailService;
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
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException(matchId));

        if (match.getTournament() == null) {
            throw new IllegalArgumentException("Match must be associated with a Tournament.");
        }

        Long tournamentIdOfMatch = match.getTournament().getId();
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

        p1.addMatchesAsPlayer1(match);
        p2.addMatchesAsPlayer2(match);
        playerService.savePlayer(p1);
        playerService.savePlayer(p2);

        try {
            emailService.sendMatchNotification(match);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        return match;
    }

    @Override
    public Match reassignPlayersToMatch(Long oldMatchId, Long newMatchId) {
        Match oldMatch = findMatchById(oldMatchId);
        Match newMatch = findMatchById(newMatchId);
    
        Player p1 = oldMatch.getPlayer1();
        Player p2 = oldMatch.getPlayer2();
        newMatch.setPlayer1(p1);
        newMatch.setPlayer2(p2);
        matchRepository.save(newMatch);

        p1.addMatchesAsPlayer1(newMatch);
        p2.addMatchesAsPlayer2(newMatch);
        playerService.savePlayer(p1);
        playerService.savePlayer(p2);

        return newMatch;
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
        return tournament.getTournamentMatchHistory();
    }

    @Override
    public List<Match> getMatchesForPlayer(Player player) {
        List<Match> matches = new ArrayList<>();
        matches.addAll(player.getMatchesAsPlayer1());
        matches.addAll(player.getMatchesAsPlayer2());
        return matches;
    }

    @Override
    public boolean sendMatchStartNotification() {
        return false;
    }

    @Override
    public HashMap<String, Boolean> viewCheckedInStatus(Match match) {
        HashMap<String, Boolean> checkInStatuses = new HashMap<>();
        checkInStatuses.put(match.getPlayer1().getUsername(), match.getStatusP1());
        checkInStatuses.put(match.getPlayer2().getUsername(), match.getStatusP2());
        return checkInStatuses;
    }
    
    public boolean bothPlayersCheckedIn(Match match) {
        return match.getStatusP1() && match.getStatusP2();
    }

    @Override
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

    @Override
    public Match convertToEntity(MatchDTO matchDTO) {
        Match match = new Match();
        match.setPlayer1(playerService.getPlayerById(matchDTO.getPlayer1Id()));
        match.setPlayer2(playerService.getPlayerById(matchDTO.getPlayer2Id()));
        match.setTournament(tournamentService.getTournamentById(matchDTO.getTournamentId()));
        match.setStatusP1(matchDTO.isStatusP1());
        match.setStatusP2(matchDTO.isStatusP2());
        match.setWinner(matchDTO.getWinnerId() != null ? playerService.getPlayerById(matchDTO.getWinnerId()) : null);
        match.setDraw(matchDTO.isDraw());
        match.setMatchStatus(matchDTO.getMatchStatus());    
        match.setOnlyEloChange(matchDTO.getEloChange());
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
                Match savedMatch = matchRepository.save(match);
                matches.add(savedMatch);

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
    public List<Match> createSingleEliminationMatches(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new TournamentNotFoundException(tournamentId));
        
        List<Player> players = playerService.getAvailablePlayersForTournament(tournamentId);
        List<Match> matches = new ArrayList<>();
        int totalPlayers = players.size();
        int playerIdx = 0;
        int matchesInRound = totalPlayers / 2;

        for (int i = 0; i < matchesInRound; i++) {
            Match match = new Match();
            match.setPlayer1(players.get(playerIdx++));
            match.setPlayer2(players.get(playerIdx++));
            match.setTournament(tournament);
            Match savedMatch = matchRepository.save(match);
            matches.add(savedMatch);

            try {
                emailService.sendMatchNotification(savedMatch);
            } catch (Exception e) {
                System.err.println("Failed to send email notification for match: " + savedMatch.getMatchId() + " - " + e.getMessage());
            }
        }

        matchesInRound = matchesInRound / 2;
        while (matchesInRound > 0) {     
            for (int i = 0; i < matchesInRound; i++) {
                Match match = new Match();
                match.setTournament(tournament);
                Match savedMatch = matchRepository.save(match);
                matches.add(savedMatch);
            }
            matchesInRound = matchesInRound / 2;
        }

        tournament.getTournamentMatchHistory().addAll(matches);
        tournamentRepository.save(tournament);

        return matches;
    }
}
