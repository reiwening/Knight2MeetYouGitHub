package com.g5.cs203proj;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.g5.cs203proj.DTO.MatchDTO;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.match.MatchNotFoundException;
import com.g5.cs203proj.exception.player.PlayerRangeException;
import com.g5.cs203proj.repository.MatchRepository;
import com.g5.cs203proj.repository.TournamentRepository;
import com.g5.cs203proj.service.MatchServiceImpl;
import com.g5.cs203proj.service.PlayerService;
import com.g5.cs203proj.service.TournamentService;
import com.g5.cs203proj.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private PlayerService playerService;
    @Mock
    private TournamentService tournamentService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private MatchServiceImpl matchService;

    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    
    private Tournament tournament;
    private Match match;
    private Match match2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        player1 = new Player("player1", "password123", "player1@test.com", "ROLE_USER");
        player1.setGlobalEloRating(1500);
        player1.setId(1L);
        
        player2 = new Player("player2", "password123", "player2@test.com", "ROLE_USER");
        player2.setGlobalEloRating(1500);
        player2.setId(2L);

        player3 = new Player("player3", "password123", "player3@test.com", "ROLE_USER");
        player3.setGlobalEloRating(1500);
        player3.setId(3L);
        
        player4 = new Player("player4", "password123", "player4@test.com", "ROLE_USER");
        player4.setGlobalEloRating(1500);
        player4.setId(4L);
        
        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setTournamentMatchHistory(new ArrayList<>());
        
        match = new Match();
        match.setMatchId(1L);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setTournament(tournament);

        match2 = new Match();
        match2.setMatchId(2L);
        match2.setPlayer1(player3);
        match2.setPlayer2(player4);
        match2.setTournament(tournament);
    }

    @Test
    void createRoundRobinMatches_Success() {
        // Arrange
        List<Player> players = Arrays.asList(player1, player2);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(players);
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        List<Match> matches = matchService.createRoundRobinMatches(1L);

        // Assert
        assertNotNull(matches);
        assertEquals(1, matches.size());
        verify(tournamentRepository).save(tournament);
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void createRoundRobinMatches_TooManyPlayers() {
        // Arrange
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            Player player = new Player("player" + i, "password123", "player" + i + "@test.com", "ROLE_USER");
            player.setId((long) i);
            players.add(player);
        }

        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(players);

        // Act & Assert
        assertThrows(PlayerRangeException.class, () -> {
            matchService.createRoundRobinMatches(1L);
        });
    }

    @Test
    void createSingleEliminationMatches_Success() {
        // Arrange
        List<Player> players = Arrays.asList(player1, player2);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(players);
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        List<Match> matches = matchService.createSingleEliminationMatches(1L);

        // Assert
        assertNotNull(matches);
        assertEquals(3, matches.size(), "A single-elimination tournament with 4 players should create 3 matches");
        verify(tournamentRepository).save(tournament);
        verify(matchRepository, times(3)).save(any(Match.class));
        verify(emailService, times(2)).sendMatchNotification(any(Match.class));
    }

    @Test
    void createSingleEliminationMatches_NotPowerOf2() {
        // Arrange
        List<Player> players = Arrays.asList(player1, player2, player3);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(players);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Match> matches = matchService.createSingleEliminationMatches(1L);

        // Assert
        assertNotNull(matches);
        assertEquals(2, matches.size(), "A single-elimination tournament with 3 players should create 2 matches at most if error not thrown.");

        // Verify that each match is saved
        verify(matchRepository, times(2)).save(any(Match.class));

        // Verify that the tournament is saved with updated matches
        verify(tournamentRepository).save(tournament);

        // Verify email notifications were sent for each match
        /*
         * email notification should not be sent since number of players is not a power of 2
         */
        verify(emailService, times(0)).sendMatchNotification(any(Match.class));
    }

    @Test
    void processMatchResult_WinnerCase() {
        // Arrange
        List<Player> players = Arrays.asList(player1, player2);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(players);
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        Match result = matchService.assignRandomPlayers(1L);

        // Assert
        assertNotNull(result);
        verify(matchRepository).save(any(Match.class));
        verify(playerService, times(2)).savePlayer(any(Player.class));
        verify(emailService).sendMatchNotification(any(Match.class));
    }

    @Test
    void bothPlayersCheckedIn_BothCheckedIn() {
        // Arrange
        match.setStatusP1(true);
        match.setStatusP2(true);

        // Act & Assert
        assertTrue(matchService.bothPlayersCheckedIn(match));
    }

    @Test
    void bothPlayersCheckedIn_OnePlayerNotCheckedIn() {
        // Arrange
        match.setStatusP1(true);
        match.setStatusP2(false);

        // Act & Assert
        assertFalse(matchService.bothPlayersCheckedIn(match));
    }
}
