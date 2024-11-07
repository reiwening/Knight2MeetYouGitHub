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
import com.g5.cs203proj.exception.*;
import com.g5.cs203proj.exception.player.PlayerRangeException;
// import com.g5.cs203proj.exception.player.InvalidPlayerRangeException;
import com.g5.cs203proj.exception.tournament.TournamentFullException;
import com.g5.cs203proj.repository.MatchRepository;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.repository.TournamentRepository;
import com.g5.cs203proj.service.TournamentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private MatchRepository matchRepository;

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

        player = new Player("testPlayer", "password123", "ROLE_USER");
        player.setGlobalEloRating(1500);

        match = new Match();
        match.setMatchId(1L);
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

        assertThrows(InvalidEloValueException.class, () -> {
            tournamentService.createTournament(tournament);
        });
    }

    @Test
    void createTournament_InvalidPlayerRange() {
        tournament.setMinPlayers(10);
        tournament.setMaxPlayers(5);

        assertThrows(PlayerRangeException.class, () -> {
            tournamentService.createTournament(tournament);
        });
    }

    @Test
    void updateTournament_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament updatedTournament = new Tournament();
        updatedTournament.setName("Updated Tournament");
        updatedTournament.setTournamentStatus("REGISTRATION");
        updatedTournament.setTournamentStyle("ROUND ROBIN");
        updatedTournament.setMinPlayers(2);
        updatedTournament.setMaxPlayers(8);
        updatedTournament.setMinElo(1000);
        updatedTournament.setMaxElo(2000);

        Tournament result = tournamentService.updateTournament(1L, updatedTournament);

        assertNotNull(result);
        assertEquals("Updated Tournament", result.getName());
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

        assertThrows(TournamentFullException.class, () -> {
            tournamentService.registerPlayer(1L, 1L);
        });
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
    void setTournamentStatus_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.setTournamentStatus(1L, "IN PROGRESS");

        assertNotNull(result);
        assertEquals("IN PROGRESS", result.getTournamentStatus());
    }

    @Test
    void setTournamentStatus_InvalidStatus() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        assertThrows(InvalidStatusException.class, () -> {
            tournamentService.setTournamentStatus(1L, "INVALID_STATUS");
        });
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
        dto.setMinElo(1000);
        dto.setMaxElo(2000);
        dto.setRegisteredPlayersId(Arrays.asList(1L));
        dto.setTournamentMatchHistoryId(Arrays.asList(1L));

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        Tournament result = tournamentService.convertToEntity(dto);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getTournamentStatus(), result.getTournamentStatus());
        assertEquals(1, result.getRegisteredPlayers().size());
        assertEquals(1, result.getTournamentMatchHistory().size());
    }

    @Test
    void startOrCancelTournament_Success_Start() {
        tournament.getRegisteredPlayers().add(player);
        tournament.getRegisteredPlayers().add(new Player("player2", "password123", "ROLE_USER"));
        
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.startOrCancelTournament(1L);

        assertNotNull(result);
        assertEquals("In Progress", result.getTournamentStatus());
    }

    @Test
    void startOrCancelTournament_Cancel() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.startOrCancelTournament(1L);

        assertNotNull(result);
        assertEquals("Cancelled", result.getTournamentStatus());
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
    void setTournamentEloRange_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.setTournamentEloRange(1L, 1200, 1800);

        assertNotNull(result);
        assertEquals(1200, result.getMinElo());
        assertEquals(1800, result.getMaxElo());
    }

    @Test
    void setTournamentPlayerRange_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.setTournamentPlayerRange(1L, 4, 16);

        assertNotNull(result);
        assertEquals(4, result.getMinPlayers());
        assertEquals(16, result.getMaxPlayers());
    }
}
