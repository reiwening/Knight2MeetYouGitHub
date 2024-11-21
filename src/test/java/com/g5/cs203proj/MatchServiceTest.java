package com.g5.cs203proj;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
import com.g5.cs203proj.exception.tournament.TournamentNotFoundException;
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
    void saveMatch_Success() {
        when(matchRepository.save(any(Match.class))).thenReturn(match);
        Match savedMatch = matchService.saveMatch(match);
        assertNotNull(savedMatch);
        assertEquals(match.getMatchId(), savedMatch.getMatchId());
        verify(matchRepository).save(match);
    }

    @Test
    void saveMatch_NullMatch() {
        assertThrows(IllegalArgumentException.class, () -> matchService.saveMatch(null));
    }

    @Test
    void deleteMatch_Success() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        matchService.deleteMatch(1L);
        verify(matchRepository).delete(match);
    }

    @Test
    void deleteMatch_NotFound() {
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());
        matchService.deleteMatch(999L);
        verify(matchRepository, never()).delete(any(Match.class));
    }

    @Test
    void findMatchById_Success() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        Match foundMatch = matchService.findMatchById(1L);
        assertNotNull(foundMatch);
        assertEquals(match.getMatchId(), foundMatch.getMatchId());
    }

    @Test
    void findMatchById_NotFound() {
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());
        Match foundMatch = matchService.findMatchById(999L);
        assertNull(foundMatch);
    }

    @Test
    void assignRandomPlayers_Success() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(playerService.getAvailablePlayersForTournament(anyLong()))
            .thenReturn(Arrays.asList(player1, player2, player3, player4));
        when(matchRepository.save(any(Match.class))).thenReturn(match);
        when(playerService.savePlayer(any(Player.class))).thenReturn(player1);

        Match result = matchService.assignRandomPlayers(1L);
        assertNotNull(result);
        assertNotNull(result.getPlayer1());
        assertNotNull(result.getPlayer2());
        verify(emailService).sendMatchNotification(any(Match.class));
    }

    @Test
    void assignRandomPlayers_MatchNotFound() {
        when(matchRepository.findById(999L)).thenThrow(new MatchNotFoundException(999L));
        assertThrows(MatchNotFoundException.class, () -> matchService.assignRandomPlayers(999L));
    }

    @Test
    void assignRandomPlayers_NotEnoughPlayers() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(playerService.getAvailablePlayersForTournament(anyLong()))
            .thenReturn(Collections.singletonList(player1));
        
        assertThrows(PlayerRangeException.class, () -> matchService.assignRandomPlayers(1L));
    }

    @Test
    void reassignPlayersToMatch_Success() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.findById(2L)).thenReturn(Optional.of(match2));
        when(matchRepository.save(any(Match.class))).thenReturn(match2);
        when(playerService.savePlayer(any(Player.class))).thenReturn(player1);

        Match reassignedMatch = matchService.reassignPlayersToMatch(1L, 2L);
        
        assertNotNull(reassignedMatch);
        assertEquals(match.getPlayer1(), reassignedMatch.getPlayer1());
        assertEquals(match.getPlayer2(), reassignedMatch.getPlayer2());
        verify(matchRepository).save(any(Match.class));
        verify(playerService, times(2)).savePlayer(any(Player.class));
    }

    @Test
    void reassignPlayersToMatch_OldMatchNotFound() {
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(MatchNotFoundException.class, () -> matchService.reassignPlayersToMatch(999L, 2L));
    }

    @Test
    void reassignPlayersToMatch_NewMatchNotFound() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(MatchNotFoundException.class, () -> matchService.reassignPlayersToMatch(1L, 999L));
    }

    @Test
    void processMatchResult_WinnerCase() {
        matchService.processMatchResult(match, player1, false);
        assertEquals("COMPLETED", match.getMatchStatus());
        assertEquals(player1, match.getWinner());
        assertFalse(match.getDraw());
    }

    @Test
    void processMatchResult_DrawCase() {
        matchService.processMatchResult(match, null, true);
        assertEquals("COMPLETED", match.getMatchStatus());
        assertNull(match.getWinner());
        assertTrue(match.getDraw());
    }

    @Test
    void processMatchResult_NullMatch() {
        assertThrows(IllegalArgumentException.class, () -> matchService.processMatchResult(null, player1, false));
    }

    @Test
    void getMatchesForTournament_Success() {
        List<Match> matches = Arrays.asList(match, match2);
        tournament.setTournamentMatchHistory(matches);
        
        List<Match> result = matchService.getMatchesForTournament(tournament);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(match));
        assertTrue(result.contains(match2));
    }

    @Test
    void getMatchesForTournament_NullTournament() {
        assertThrows(IllegalArgumentException.class, () -> matchService.getMatchesForTournament(null));
    }

    @Test
    void getMatchesForPlayer_Success() {
        player1.addMatchesAsPlayer1(match);
        player1.addMatchesAsPlayer2(match2);

        List<Match> result = matchService.getMatchesForPlayer(player1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(match));
        assertTrue(result.contains(match2));
    }

    @Test
    void getMatchesForPlayer_NullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> matchService.getMatchesForPlayer(null));
    }

    @Test
    void viewCheckedInStatus_Success() {
        match.setStatusP1(true);
        match.setStatusP2(false);

        HashMap<String, Boolean> status = matchService.viewCheckedInStatus(match);

        assertNotNull(status);
        assertEquals(2, status.size());
        assertTrue(status.get(player1.getUsername()));
        assertFalse(status.get(player2.getUsername()));
    }

    @Test
    void viewCheckedInStatus_NullMatch() {
        assertThrows(IllegalArgumentException.class, () -> matchService.viewCheckedInStatus(null));
    }

    @Test
    void createRoundRobinMatches_Success() {
        List<Player> players = Arrays.asList(player1, player2, player3);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(players);
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        List<Match> matches = matchService.createRoundRobinMatches(1L);

        assertNotNull(matches);
        assertEquals(3, matches.size(), "3 players should create 3 matches in round-robin");
        verify(tournamentRepository).save(tournament);
        verify(matchRepository, times(3)).save(any(Match.class));
    }

    @Test
    void createRoundRobinMatches_TournamentNotFound() {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(TournamentNotFoundException.class, () -> matchService.createRoundRobinMatches(999L));
    }

    @Test
    void createSingleEliminationMatches_Success() {
        List<Player> players = Arrays.asList(player1, player2, player3, player4);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(players);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match m = invocation.getArgument(0);
            m.setMatchId((long) (tournament.getTournamentMatchHistory().size() + 1));
            return m;
        });

        List<Match> matches = matchService.createSingleEliminationMatches(1L);

        assertNotNull(matches);
        assertEquals(3, matches.size(), "4 players should create 3 matches in single elimination");
        verify(tournamentRepository).save(tournament);
        verify(matchRepository, times(3)).save(any(Match.class));
    }

    @Test
    void createSingleEliminationMatches_TournamentNotFound() {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(TournamentNotFoundException.class, () -> matchService.createSingleEliminationMatches(999L));
    }

    @Test
    void createSingleEliminationMatches_NoPlayers() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(Collections.emptyList());

        List<Match> matches = matchService.createSingleEliminationMatches(1L);
        assertTrue(matches.isEmpty());
    }

    @Test
    void convertToDTO_Success() {
        match.setWinner(player1);
        match.setDraw(false);
        match.setMatchStatus("COMPLETED");
        match.setOnlyEloChange(15.0);

        MatchDTO dto = matchService.convertToDTO(match);

        assertNotNull(dto);
        assertEquals(match.getMatchId(), dto.getId());
        assertEquals(match.getPlayer1().getId(), dto.getPlayer1Id());
        assertEquals(match.getPlayer2().getId(), dto.getPlayer2Id());
        assertEquals(match.getTournament().getId(), dto.getTournamentId());
        assertEquals(match.getStatusP1(), dto.isStatusP1());
        assertEquals(match.getStatusP2(), dto.isStatusP2());
        assertEquals(match.getWinner().getId(), dto.getWinnerId());
        assertEquals(match.getDraw(), dto.isDraw());
        assertEquals(match.getMatchStatus(), dto.getMatchStatus());
        assertEquals(match.getEloChange(), dto.getEloChange());
    }

    @Test
    void convertToDTO_NullMatch() {
        assertThrows(IllegalArgumentException.class, () -> matchService.convertToDTO(null));
    }

    @Test
    void convertToEntity_Success() {
        MatchDTO dto = new MatchDTO();
        dto.setId(1L);
        dto.setPlayer1Id(1L);
        dto.setPlayer2Id(2L);
        dto.setTournamentId(1L);
        dto.setStatusP1(true);
        dto.setStatusP2(false);
        dto.setWinnerId(1L);
        dto.setDraw(false);
        dto.setMatchStatus("COMPLETED");
        dto.setEloChange(15.0);

        when(playerService.getPlayerById(1L)).thenReturn(player1);
        when(playerService.getPlayerById(2L)).thenReturn(player2);
        when(tournamentService.getTournamentById(1L)).thenReturn(tournament);

        Match entity = matchService.convertToEntity(dto);

        assertNotNull(entity);
        assertEquals(player1, entity.getPlayer1());
        assertEquals(player2, entity.getPlayer2());
        assertEquals(tournament, entity.getTournament());
        assertTrue(entity.getStatusP1());
        assertFalse(entity.getStatusP2());
        assertEquals(player1, entity.getWinner());
        assertFalse(entity.getDraw());
        assertEquals("COMPLETED", entity.getMatchStatus());
        assertEquals(15.0, entity.getEloChange());
    }

    @Test
    void convertToEntity_NullDTO() {
        assertThrows(IllegalArgumentException.class, () -> matchService.convertToEntity(null));
    }

    @Test
    void isPowerOfTwo_Success() {
        assertTrue(matchService.isPowerOfTwo(2));
        assertTrue(matchService.isPowerOfTwo(4));
        assertTrue(matchService.isPowerOfTwo(8));
        assertTrue(matchService.isPowerOfTwo(16));
    }

    @Test
    void isPowerOfTwo_Failure() {
        assertFalse(matchService.isPowerOfTwo(0));
        assertFalse(matchService.isPowerOfTwo(3));
        assertFalse(matchService.isPowerOfTwo(5));
        assertFalse(matchService.isPowerOfTwo(7));
    }
}
