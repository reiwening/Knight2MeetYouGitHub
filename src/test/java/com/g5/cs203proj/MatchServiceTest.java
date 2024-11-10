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
import org.springframework.mail.SimpleMailMessage;

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
    private PlayerService playerService;
    @Mock
    private TournamentService tournamentService;
    @Mock
    private TournamentRepository tournamentRepository;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize test data
        player1 = new Player("player1", "password123", "player1@test.com", "ROLE_USER");
        player1.setGlobalEloRating(1500);
        
        player2 = new Player("player2", "password123", "player2@test.com", "ROLE_USER");
        player2.setGlobalEloRating(1500);

        player3 = new Player("player3", "password123", "player3@test.com", "ROLE_USER");
        player3.setGlobalEloRating(1500);
        
        player4 = new Player("player4", "password123", "player4@test.com", "ROLE_USER");
        player4.setGlobalEloRating(1500);
        
        tournament = new Tournament();
        tournament.setId(1L);
        
        match = new Match();
        match.setMatchId(1L);
        match.setPlayer1(player1);
        match.setPlayer2(player2);

        match = new Match();
        match.setMatchId(2L);
        match.setPlayer1(player3);
        match.setPlayer2(player4);
        match.setTournament(tournament);
    }

    @Test
    void assignRandomPlayers_twoPlayers_ReturnMatch() {
        // Arrange
        Match match = new Match();
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        match.setTournament(tournament);
        Player p1 = new Player("testPlayer1", "password123", "test1@test.com", "ROLE_USER");
        Player p2 = new Player("testPlayer2", "password123", "test2@test.com", "ROLE_USER");
        
        List<Player> availablePlayers = new ArrayList<>(List.of(p1,p2));

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(availablePlayers);

        // Act
        Match result = matchService.assignRandomPlayers(1L);

        // Assert
        assertNotNull(result);
        assertTrue(
            (p1.equals(result.getPlayer1()) && p2.equals(result.getPlayer2())) ||
            (p2.equals(result.getPlayer1()) && p1.equals(result.getPlayer2()))
        );

        verify(matchRepository, times(1)).findById(1L);
        verify(playerService, times(1)).getAvailablePlayersForTournament(1L);
        verify(matchRepository, times(1)).save(match);
        verify(playerService, times(1)).savePlayer(p1);
        verify(playerService, times(1)).savePlayer(p2);
    }

    @Test
    void assignRandomPlayers_NotEnoughPlayers_ThrowEx() {
        // Arrange
        Match match = new Match();
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        match.setTournament(tournament);

        List<Player> availablePlayers = new ArrayList<>();

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(availablePlayers);

        // Act & Assert
        assertThrows(PlayerRangeException.class, () -> {
            matchService.assignRandomPlayers(1L);
        });

        verify(matchRepository, times(1)).findById(1L);
        verify(playerService, times(1)).getAvailablePlayersForTournament(1L);
        verify(matchRepository, times(0)).save(any(Match.class)); 
    }

    @Test
    void assignRandomPlayers_MatchNotFound_ThrowEx() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(MatchNotFoundException.class, () -> {
            matchService.assignRandomPlayers(1L);
        });

        verify(matchRepository, times(1)).findById(1L);
        verify(playerService, times(0)).getAvailablePlayersForTournament(anyLong());
        verify(matchRepository, times(0)).save(any(Match.class));
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
    }

    @Test
    void createRoundRobinMatches_TooManyPlayers() {
        // Arrange
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            players.add(new Player("player" + i, "password123", "player" + i + "@test.com", "ROLE_USER"));
        }
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(players);

        // Act & Assert
        assertThrows(PlayerRangeException.class, () -> {
            matchService.createRoundRobinMatches(1L);
        });
    }

    @Test
    void createSingleEliminationMatches_Success() {
        // Arrange
        List<Player> players = Arrays.asList(player1, player2, player3, player4);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(players);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Match> matches = matchService.createSingleEliminationMatches(1L);

        // Assert
        assertNotNull(matches);
        assertEquals(3, matches.size(), "A single-elimination tournament with 4 players should create 3 matches, 2 semi-finals and 1 finals");

        // Verify that each match is saved
        verify(matchRepository, times(3)).save(any(Match.class));

        // Verify that the tournament is saved with updated matches
        verify(tournamentRepository).save(tournament);

        // Verify email notifications were sent for each match
        /*
         * email notification should only be sent twice since creating the single elimination matches
         * creates all the matches but only populates the first round with players
         */
        verify(emailService, times(2)).sendMatchNotification(any(Match.class));
    }

    @Test
    void processMatchResult_WinnerCase() {
        // Arrange
        Match match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);

        // Act
        matchService.processMatchResult(match, player1, false);

        // Assert
        assertEquals("COMPLETED", match.getMatchStatus());
        assertEquals(player1, match.getWinner());
        assertFalse(match.getDraw());
    }

    @Test
    void processMatchResult_DrawCase() {
        // Arrange
        Match match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);

        // Act
        matchService.processMatchResult(match, null, true);

        // Assert
        assertEquals("COMPLETED", match.getMatchStatus());
        assertNull(match.getWinner());
        assertTrue(match.getDraw());
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

    @Test
    void convertToDTO_Success() {
        // Arrange
        match.setMatchStatus("PENDING");
        match.setDraw(false);
        match.setWinner(player1);

        // Act
        MatchDTO dto = matchService.convertToDTO(match);

        // Assert
        assertEquals(match.getMatchId(), dto.getId());
        assertEquals(player1.getId(), dto.getPlayer1Id());
        assertEquals(player2.getId(), dto.getPlayer2Id());
        assertEquals(tournament.getId(), dto.getTournamentId());
        assertEquals(match.getMatchStatus(), dto.getMatchStatus());
        assertEquals(match.getDraw(), dto.isDraw());
        assertEquals(player1.getId(), dto.getWinnerId());
    }

    @Test
    void convertToEntity_Success() {
        // Arrange
        MatchDTO dto = new MatchDTO();
        dto.setId(1L);
        dto.setPlayer1Id(1L);
        dto.setPlayer2Id(2L);
        dto.setTournamentId(1L);
        dto.setMatchStatus("PENDING");
        dto.setDraw(false);
        dto.setWinnerId(1L);

        when(playerService.getPlayerById(1L)).thenReturn(player1);
        when(playerService.getPlayerById(2L)).thenReturn(player2);
        when(tournamentService.getTournamentById(1L)).thenReturn(tournament);

        // Act
        Match result = matchService.convertToEntity(dto);

        // Assert
        assertEquals(player1, result.getPlayer1());
        assertEquals(player2, result.getPlayer2());
        assertEquals(tournament, result.getTournament());
        assertEquals(dto.getMatchStatus(), result.getMatchStatus());
        assertEquals(dto.isDraw(), result.getDraw());
    }

    @Test
    void saveMatch_Success() {
        // Arrange
        when(matchRepository.save(match)).thenReturn(match);

        // Act
        Match savedMatch = matchService.saveMatch(match);

        // Assert
        assertNotNull(savedMatch);
        verify(matchRepository).save(match);
    }

    @Test
    void deleteMatch_Success() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Act
        matchService.deleteMatch(1L);

        // Assert
        verify(matchRepository).delete(match);
    }

    @Test
    void findMatchById_Success() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Act
        Match foundMatch = matchService.findMatchById(1L);

        // Assert
        assertNotNull(foundMatch);
        assertEquals(match, foundMatch);
    }

    @Test
    void findMatchById_NotFound() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Match foundMatch = matchService.findMatchById(1L);

        // Assert
        assertNull(foundMatch);
    }

//     @Test
// public void testEmailNotificationOnMatchCreation() {
//     // Arrange: Create two players and add them to a list of available players
//     Player player1 = new Player("playerOne", "password123", "playerOne@example.com", "ROLE_USER");
//     player1.setId(1L);
//     Player player2 = new Player("playerTwo", "password123", "playerTwo@example.com", "ROLE_USER");
//     player2.setId(2L);

//     List<Player> availablePlayers = new ArrayList<>(List.of(player1, player2));
//     when(playerService.getAvailablePlayersForTournament(anyLong())).thenReturn(availablePlayers);

//     // Arrange: Create a match and mock its retrieval and saving
//     Match match = new Match();
//     match.setMatchId(1L);
//     match.setTournament(new Tournament());  // Only if the tournament is needed within the method
//     when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
//     when(matchRepository.save(any(Match.class))).thenReturn(match);

//     // Act: Use assignRandomPlayers to assign players and trigger the email notification
//     matchService.assignRandomPlayers(match.getMatchId());

//     // Assert: Verify that the emailService.sendMatchNotification was called
//     verify(emailService, times(1)).sendMatchNotification(match);
// }


   
}
