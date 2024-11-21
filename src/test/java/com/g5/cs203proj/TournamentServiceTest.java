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
import com.g5.cs203proj.enums.Statuses;
import com.g5.cs203proj.exception.inputs.InvalidEloValueException;
import com.g5.cs203proj.exception.inputs.InvalidStyleException;
import com.g5.cs203proj.exception.player.PlayerAvailabilityException;
import com.g5.cs203proj.exception.tournament.*;
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
    void createTournament_Success() {
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
        Tournament result = tournamentService.createTournament(tournament);
        assertNotNull(result);
        assertEquals(tournament.getName(), result.getName());
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void createTournament_InvalidEloRange() {
        tournament.setMinElo(2000);
        tournament.setMaxElo(1000);
        assertThrows(InvalidEloValueException.class, () -> tournamentService.createTournament(tournament));
    }

    @Test
    void createTournament_InvalidStyle() {
        tournament.setTournamentStyle("INVALID_STYLE");
        assertThrows(InvalidStyleException.class, () -> tournamentService.createTournament(tournament));
    }

    @Test
    void updateTournament_Success() {
        Tournament updatedTournament = new Tournament();
        updatedTournament.setName("Updated Tournament");
        updatedTournament.setTournamentStatus("REGISTRATION");
        updatedTournament.setTournamentStyle("ROUND ROBIN");
        updatedTournament.setMinPlayers(2);
        updatedTournament.setMaxPlayers(8);
        updatedTournament.setMinElo(1000);
        updatedTournament.setMaxElo(2000);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(updatedTournament);

        Tournament result = tournamentService.updateTournament(1L, updatedTournament);
        assertNotNull(result);
        assertEquals("Updated Tournament", result.getName());
    }

    @Test
    void updateTournament_NotFound() {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(TournamentNotFoundException.class, 
            () -> tournamentService.updateTournament(999L, tournament));
    }

    @Test
    void deleteTournament_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        doNothing().when(tournamentRepository).delete(tournament);
        
        tournamentService.deleteTournament(1L);
        verify(tournamentRepository).delete(tournament);
    }

    @Test
    void deleteTournament_NotFound() {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(TournamentNotFoundException.class, () -> tournamentService.deleteTournament(999L));
    }

    @Test
    void getTournamentById_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        Tournament result = tournamentService.getTournamentById(1L);
        assertNotNull(result);
        assertEquals(tournament.getId(), result.getId());
    }

    @Test
    void getTournamentById_NotFound() {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(TournamentNotFoundException.class, () -> tournamentService.getTournamentById(999L));
    }

    @Test
    void getAllTournaments_Success() {
        List<Tournament> tournaments = Arrays.asList(tournament);
        when(tournamentRepository.findAll()).thenReturn(tournaments);
        
        List<Tournament> result = tournamentService.getAllTournaments();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllRegisterableTournaments_Success() {
        List<Tournament> tournaments = Arrays.asList(tournament);
        when(tournamentRepository.findByTournamentStatus("registration")).thenReturn(tournaments);
        
        List<Tournament> result = tournamentService.getAllRegisterableTournaments();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void registerPlayer_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.registerPlayer(1L, 1L);
        assertNotNull(result);
        assertTrue(result.getRegisteredPlayers().contains(player));
    }

    @Test
    void registerPlayer_TournamentFull() {
        tournament.setMaxPlayers(1);
        tournament.getRegisteredPlayers().add(new Player());
        
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        assertThrows(TournamentFullException.class, () -> tournamentService.registerPlayer(1L, 1L));
    }

    @Test
    void removePlayer_Success() {
        tournament.getRegisteredPlayers().add(player);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.removePlayer(1L, 1L);
        assertNotNull(result);
        assertFalse(result.getRegisteredPlayers().contains(player));
    }

    @Test
    void removePlayer_NotInTournament() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        assertThrows(PlayerAvailabilityException.class, () -> tournamentService.removePlayer(1L, 1L));
    }

    @Test
    void isUserAllowedToDeletePlayer_Success() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        boolean result = tournamentService.isUserAllowedToDeletePlayer(1L, "testPlayer");
        assertTrue(result);
    }

    @Test
    void getRegisteredPlayers_Success() {
        tournament.getRegisteredPlayers().add(player);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        Set<Player> result = tournamentService.getRegisteredPlayers(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(player));
    }

    @Test
    void setTournamentEloRange_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.setTournamentEloRange(1L, 1200, 1800);
        assertEquals(1200, result.getMinElo());
        assertEquals(1800, result.getMaxElo());
    }

    @Test
    void setTournamentStatus_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.setTournamentStatus(1L, Statuses.IN_PROGRESS.getDisplayName());

        assertNotNull(result);
        assertEquals(Statuses.IN_PROGRESS.getDisplayName(), result.getTournamentStatus());
    }

    @Test
    void setTournamentStyle_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.setTournamentStyle(1L, "SINGLE ELIMINATION");
        assertEquals("SINGLE ELIMINATION", result.getTournamentStyle());
    }

    @Test
    void setTournamentPlayerRange_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.setTournamentPlayerRange(1L, 4, 16);
        assertEquals(4, result.getMinPlayers());
        assertEquals(16, result.getMaxPlayers());
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

        List<Match> matches = Arrays.asList(completedMatch1, completedMatch2, nextRoundMatch);
        tournament.setTournamentMatchHistory(matches);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(emailService).sendMatchNotification(any(Match.class));

        List<Match> result = tournamentService.processSingleEliminationRound(1L);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(tournamentRepository, times(2)).save(tournament);
        verify(emailService).sendMatchNotification(any(Match.class));

        Match finalMatch = result.get(2);
        assertNotNull(finalMatch.getPlayer1());
        assertNotNull(finalMatch.getPlayer2());
        assertEquals("NOT_STARTED", finalMatch.getMatchStatus());
    }

    @Test
    void getWinnersForCurrentRound_Success() {
        // Setup players
        Player winner = new Player("winner", "password123", "winner@test.com", "ROLE_USER");
        winner.setId(1L);
        Player loser = new Player("loser", "password123", "loser@test.com", "ROLE_USER");
        loser.setId(2L);

        // Setup matches with consistent winner
        Match match1 = new Match();
        match1.setMatchId(1L);
        match1.setPlayer1(winner);
        match1.setPlayer2(loser);
        match1.setMatchStatus("COMPLETED");
        match1.setWinner(winner);

        Match match2 = new Match();
        match2.setMatchId(2L);
        match2.setPlayer1(winner);
        match2.setPlayer2(loser);
        match2.setMatchStatus("COMPLETED");
        match2.setWinner(winner);

        // Set up tournament
        tournament.setRegisteredPlayers(new HashSet<>(Arrays.asList(winner, loser)));
        tournament.setTournamentMatchHistory(Arrays.asList(match1, match2));
        tournament.setRoundNumber(1);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        List<Player> winners = tournamentService.getWinnersForCurrentRound(1L, 1);
        
        assertNotNull(winners);
        assertEquals(1, winners.size());
        assertEquals(winner, winners.get(0));
    }

    @Test
    void convertToDTO_Success() {
        tournament.getRegisteredPlayers().add(player);
        tournament.getTournamentMatchHistory().add(match);

        TournamentDTO dto = tournamentService.convertToDTO(tournament);

        assertNotNull(dto);
        assertEquals(tournament.getId(), dto.getTournamentId());
        assertEquals(tournament.getName(), dto.getName());
        assertEquals(1, dto.getRegisteredPlayersId().size());
        assertEquals(1, dto.getTournamentMatchHistoryId().size());
    }

    @Test
    void convertToEntity_Success() {
        TournamentDTO dto = new TournamentDTO();
        dto.setName("Test Tournament");
        dto.setTournamentStatus("REGISTRATION");
        dto.setTournamentStyle("ROUND ROBIN");
        dto.setMinPlayers(2);
        dto.setMaxPlayers(8);
        dto.setRegisteredPlayersId(Arrays.asList(1L));
        dto.setTournamentMatchHistoryId(Arrays.asList(1L));

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        Tournament entity = tournamentService.convertToEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getTournamentStatus(), entity.getTournamentStatus());
        assertEquals(1, entity.getRegisteredPlayers().size());
        assertEquals(1, entity.getTournamentMatchHistory().size());
    }
}
