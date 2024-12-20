package com.g5.cs203proj;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.player.PlayerAvailabilityException;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.service.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private TokenService tokenService;

    @Mock
    private MatchService matchService;

    @Mock
    private Authentication authentication;

    private PlayerServiceImpl playerService;

    private Player player;
    private Tournament tournament;
    private Match match;

    @BeforeEach
    void setUp() {
        // Initialize PlayerServiceImpl with all required dependencies
        playerService = new PlayerServiceImpl(
            playerRepository,
            bCryptPasswordEncoder,
            tournamentService,
            emailService,
            tokenService
        );

        player = new Player("testUser", "password123", "testuser@test.com", "ROLE_USER");
        player.setId(1L);
        player.setGlobalEloRating(1500);

        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Test Tournament");

        match = new Match();
        match.setMatchId(1L);
        match.setPlayer1(player);
    }

    @Test
    void savePlayer_Success() {
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        Player savedPlayer = playerService.savePlayer(player);

        assertNotNull(savedPlayer);
        assertEquals(player.getUsername(), savedPlayer.getUsername());
        verify(playerRepository).save(player);
    }

    @Test
    void findPlayerByUsername_Success() {
        when(playerRepository.findByUsername("testUser")).thenReturn(Optional.of(player));

        Optional<Player> foundPlayer = playerService.findPlayerByUsername("testUser");

        assertTrue(foundPlayer.isPresent());
        assertEquals("testUser", foundPlayer.get().getUsername());
    }

    @Test
    void findPlayerByUsername_NotFound() {
        when(playerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<Player> foundPlayer = playerService.findPlayerByUsername("nonexistent");

        assertFalse(foundPlayer.isPresent());
    }

    @Test
    void getAllPlayers_Success() {
        List<Player> players = Arrays.asList(player);
        when(playerRepository.findAll()).thenReturn(players);

        List<Player> result = playerService.getAllPlayers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(player, result.get(0));
    }

    @Test
    void getAllAdmins_Success() {
        Player adminPlayer = new Player("admin", "password123", "admin@test.com", "ROLE_ADMIN");
        List<Player> players = Arrays.asList(player, adminPlayer);
        when(playerRepository.findAll()).thenReturn(players);

        List<Player> admins = playerService.getAllAdmins();

        assertEquals(1, admins.size());
        assertEquals("admin", admins.get(0).getUsername());
    }

    @Test
    void getAllPlayerUsers_Success() {
        Player adminPlayer = new Player("admin", "password123", "admin@test.com", "ROLE_ADMIN");
        List<Player> players = Arrays.asList(player, adminPlayer);
        when(playerRepository.findAll()).thenReturn(players);

        List<Player> users = playerService.getAllPlayerUsers();

        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());
    }

    @Test
    void setPlayerGlobalEloRating_Success() {
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        playerService.setPlayerGlobalEloRating(player, 1600);

        assertEquals(1600, player.getGlobalEloRating());
        verify(playerRepository).save(player);
    }

    @Test
    void getAvailablePlayersForTournament_Success() {
        List<Player> availablePlayers = Arrays.asList(player);
        when(playerRepository.findAllByTournamentIdAndNotInOngoingMatch(1L)).thenReturn(availablePlayers);

        List<Player> result = playerService.getAvailablePlayersForTournament(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(player, result.get(0));
    }

    @Test
    void registerPlayer_Success() {
        Player newPlayer = new Player("newUser", "password123", "newuser@test.com", "ROLE_USER");
        when(playerRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(playerRepository.save(any(Player.class))).thenReturn(newPlayer);
        when(tokenService.generateEmailVerificationToken(any(Player.class))).thenReturn("token123");

        Player registeredPlayer = playerService.registerPlayer(newPlayer);

        assertNotNull(registeredPlayer);
        assertEquals("newUser", registeredPlayer.getUsername());
        verify(bCryptPasswordEncoder).encode("password123");
        verify(emailService).sendRegisterNotification(any(Player.class), eq("token123"));
    }

    @Test
    void registerPlayer_UsernameExists() {
        when(playerRepository.findByUsername("testUser")).thenReturn(Optional.of(player));

        Player result = playerService.registerPlayer(player);

        assertNull(result);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void deletePlayer_Success() {
        when(playerRepository.findByUsername("testUser")).thenReturn(Optional.of(player));

        playerService.deletePlayer("testUser");

        verify(playerRepository).delete(player);
    }

    @Test
    void deletePlayer_NotFound() {
        when(playerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(PlayerAvailabilityException.class, () -> {
            playerService.deletePlayer("nonexistent");
        });
    }

    @Test
    void getTournamentRegistered_Success() {
        Set<Tournament> tournaments = new HashSet<>(Arrays.asList(tournament));
        player.setTournamentRegistered(tournaments);

        Set<Tournament> result = playerService.getTournamentRegistered(player);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(tournament));
    }

    @Test
    void validateUserAccess_Success() {
        when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertDoesNotThrow(() -> playerService.validateUserAccess("testUser"));
    }

    @Test
    void validateUserAccess_Unauthorized() {
        when(authentication.getName()).thenReturn("otherUser");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(AccessDeniedException.class, () -> 
            playerService.validateUserAccess("testUser")
        );
    }



    @Test
    void getPlayerGlobalEloRating_Success() {
        double result = playerService.getPlayerGlobalEloRating(player);

        assertEquals(1500, result);
    }
}
