package com.g5.cs203proj;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.StackWalker.Option;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.MatchNotFoundException;
import com.g5.cs203proj.exception.NotEnoughPlayersException;
import com.g5.cs203proj.repository.MatchRepository;
import com.g5.cs203proj.service.MatchServiceImpl;
import com.g5.cs203proj.service.PlayerService;
import com.g5.cs203proj.service.TournamentService;

// import com.g5.cs203proj.repository.MatchRepository;
// import com.g5.cs203proj.service.PlayerService;
// import com.g5.cs203proj.service.TournamentService;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private PlayerService playerService;
    @Mock
    private TournamentService tournamentService;

    @InjectMocks
    private MatchServiceImpl matchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void assignRandomPlayers_twoPlayers_ReturnMatch() {
        // Arrange
        Match match = new Match();
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        match.setTournament(tournament);
        Player p1 = new Player();
        Player p2 = new Player();
        
        List<Player> availablePlayers = new ArrayList<>(List.of(p1,p2)); // Collections.shuffle(availablePlayers) there will be modification so we use ArrayList

        // arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(availablePlayers);

        // Act
        Match result = matchService.assignRandomPlayers(1L);

        // Arrange
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

        List<Player> availablePlayers = new ArrayList<>();  // no players available

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(playerService.getAvailablePlayersForTournament(1L)).thenReturn(availablePlayers);

        assertThrows(NotEnoughPlayersException.class, () -> {
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
        // Act Assert 
        assertThrows(MatchNotFoundException.class, () -> {
            matchService.assignRandomPlayers(1L);
        });

        verify(matchRepository, times(1)).findById(1L);
        verify(playerService, times(0)).getAvailablePlayersForTournament(anyLong());  
        verify(matchRepository, times(0)).save(any(Match.class));  

    }



    
}
