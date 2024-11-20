package com.g5.cs203proj;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.inputs.InvalidEloValueException;
import com.g5.cs203proj.exception.inputs.InvalidStatusException;
import com.g5.cs203proj.exception.inputs.InvalidStyleException;
import com.g5.cs203proj.exception.player.PlayerAvailabilityException;
import com.g5.cs203proj.exception.player.PlayerRangeException;
import com.g5.cs203proj.exception.tournament.TournamentFullException;
import com.g5.cs203proj.exception.tournament.TournamentNotFoundException;
import com.g5.cs203proj.repository.MatchRepository;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.repository.TournamentRepository;
import com.g5.cs203proj.service.EmailService;
import com.g5.cs203proj.service.TournamentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private Tournament tournament;
    private Player player;
    private Match match;

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Test Tournament");
        tournament.setTournamentStatus("REGISTRATION");
        tournament.setTournamentStyle("ROUND ROBIN");
        tournament.setMinPlayers(2);
        tournament.setMaxPlayers(8);
        tournament.setMinElo(1000);
        tournament.setMaxElo(2000);
        tournament.setRegistrationCutOff(LocalDateTime.now().plusDays(7));
        tournament.setRegisteredPlayers(new HashSet<>());
        tournament.setTournamentMatchHistory(new ArrayList<>());
        tournament.setRoundNumber(1);

        player = new Player("testPlayer", "password123", "testplayer@test.com", "ROLE_USER");
        player.setId(1L);
        player.setGlobalEloRating(1500);

        match = new Match();
        match.setMatchId(1L);
        match.setMatchStatus("NOT_STARTED");
    }

    @Test
    void processSingleEliminationRound_Success() {
        // Setup players
        Player player1 = new Player("player1", "password123", "player1@test.com", "ROLE_USER");
        Player player2 = new Player("player2", "password123", "player2@test.com", "ROLE_USER");
        Player player3 = new Player("player3", "password123", "player3@test.com", "ROLE_USER");
        Player player4 = new Player("player4", "password123", "player4@test.com", "ROLE_USER");
        
        player1.setId(1L);
        player2.setId(2L);
        player3.setId(3L);
        player4.setId(4L);

        Set<Player> players = new HashSet<>(Arrays.asList(player1, player2, player3, player4));
        tournament.setRegisteredPlayers(players);

        // Setup first round completed matches with winners
        Match completedMatch1 = new Match();
        completedMatch1.setMatchId(1L);
        completedMatch1.setPlayer1(player1);
        completedMatch1.setPlayer2(player2);
        completedMatch1.setMatchStatus("COMPLETED");
        completedMatch1.setWinner(player1);
        completedMatch1.setTournament(tournament);

        Match completedMatch2 = new Match();
        completedMatch2.setMatchId(2L);
        completedMatch2.setPlayer1(player3);
        completedMatch2.setPlayer2(player4);
        completedMatch2.setMatchStatus("COMPLETED");
        completedMatch2.setWinner(player3);
        completedMatch2.setTournament(tournament);

        // Setup next round match
        Match nextRoundMatch = new Match();
        nextRoundMatch.setMatchId(3L);
        nextRoundMatch.setMatchStatus("NOT_STARTED");
        nextRoundMatch.setTournament(tournament);

        // Set tournament match history
        List<Match> matches = Arrays.asList(completedMatch1, completedMatch2, nextRoundMatch);
        tournament.setTournamentMatchHistory(matches);

        // Mock repository responses
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArguments()[0]);
        
        // Mock email service to do nothing (avoid NullPointerException)
        doNothing().when(emailService).sendMatchNotification(any(Match.class));

        // Act
        List<Match> result = tournamentService.processSingleEliminationRound(1L);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(tournamentRepository, times(2)).save(tournament);
        verify(emailService).sendMatchNotification(any(Match.class));

        // Verify the next round match has players assigned
        Match finalMatch = result.get(2);
        assertNotNull(finalMatch.getPlayer1());
        assertNotNull(finalMatch.getPlayer2());
        assertEquals("NOT_STARTED", finalMatch.getMatchStatus());
    }

    @Test
    void processSingleEliminationRound_NoNextRoundMatch() {
        // Setup completed match with no next round match
        Match completedMatch = new Match();
        completedMatch.setMatchId(1L);
        completedMatch.setMatchStatus("COMPLETED");
        completedMatch.setTournament(tournament);

        tournament.setTournamentMatchHistory(Arrays.asList(completedMatch));

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        assertThrows(IllegalStateException.class, () -> {
            tournamentService.processSingleEliminationRound(1L);
        });
    }
}
