package com.g5.cs203proj;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.RandomRanking;
import com.g5.cs203proj.entity.Ranking;
import com.g5.cs203proj.entity.RoundRobinRanking;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.enums.Styles;
import com.g5.cs203proj.service.RankingService;

@ExtendWith(MockitoExtension.class)
public class RankingServiceTest {
    
    @InjectMocks
    private RankingService rankingService;
    
    private Tournament tournament;
    private Player player1;
    private Player player2;
    private Match match;
    private List<Ranking> rankings;
    
    @BeforeEach
    void setUp() {
        // Create tournament
        tournament = new Tournament();
        tournament.setTournamentStyle(Styles.ROUND_ROBIN.getDisplayName());
        
        // Create players
        player1 = new Player("player1", "pass", "p1@test.com", "ROLE_USER");
        player1.setId(1L);
        player2 = new Player("player2", "pass", "p2@test.com", "ROLE_USER");
        player2.setId(2L);
        
        // Create match
        match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setTournament(tournament);
        
        // Initialize rankings list
        rankings = new ArrayList<>();
    }

    @Test
    void updateRankings_RoundRobin_WinnerCase() {
        // Arrange
        tournament.setTournamentStyle(Styles.ROUND_ROBIN.getDisplayName());
        rankings.add(new RoundRobinRanking(tournament, player1));
        rankings.add(new RoundRobinRanking(tournament, player2));
        tournament.setRankings(rankings);
        
        match.setWinner(player1);
        match.setDraw(false);

        // Act
        List<Ranking> updatedRankings = rankingService.updateRankings(tournament, match);

        // Assert
        RoundRobinRanking winner = (RoundRobinRanking) updatedRankings.get(0);
        RoundRobinRanking loser = (RoundRobinRanking) updatedRankings.get(1);
        assertEquals(3, winner.getPoints());  // Win = 3 points
        assertEquals(0, loser.getPoints());   // Loss = 0 points
        assertEquals(1, winner.getRank());
        assertEquals(2, loser.getRank());
    }

    @Test
    void updateRankings_RoundRobin_DrawCase() {
        // Arrange
        tournament.setTournamentStyle(Styles.ROUND_ROBIN.getDisplayName());
        rankings.add(new RoundRobinRanking(tournament, player1));
        rankings.add(new RoundRobinRanking(tournament, player2));
        tournament.setRankings(rankings);
        
        match.setDraw(true);

        // Act
        List<Ranking> updatedRankings = rankingService.updateRankings(tournament, match);

        // Assert
        RoundRobinRanking player1Ranking = (RoundRobinRanking) updatedRankings.get(0);
        RoundRobinRanking player2Ranking = (RoundRobinRanking) updatedRankings.get(1);
        assertEquals(1, player1Ranking.getPoints());  // Draw = 1 point
        assertEquals(1, player2Ranking.getPoints());  // Draw = 1 point
        assertEquals(1, player1Ranking.getRank());    // Same rank due to tie
        assertEquals(1, player2Ranking.getRank());    // Same rank due to tie
    }

    @Test
    void updateRankings_SingleElimination_FirstRound() {
        // Arrange
        tournament.setTournamentStyle(Styles.RANDOM.getDisplayName());
        tournament.setRoundNumber(1);
        
        // Create 8-player tournament (3 rounds total)
        Set<Player> players = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            Player p = new Player("player" + i, "pass", "p" + i + "@test.com", "ROLE_USER");
            players.add(p);
        }
        tournament.setRegisteredPlayers(players);
        
        // Add rankings for match players
        rankings.add(new RandomRanking(tournament, player1));
        rankings.add(new RandomRanking(tournament, player2));
        tournament.setRankings(rankings);
        
        match.setWinner(player1);
        match.setDraw(false);

        // Act
        List<Ranking> updatedRankings = rankingService.updateRankings(tournament, match);

        // Assert
        RandomRanking winner = (RandomRanking) updatedRankings.get(0);
        RandomRanking loser = (RandomRanking) updatedRankings.get(1);
        assertEquals(1, winner.getRank());    // Winner stays in tournament
        assertEquals(5, loser.getRank());     // 2^(3-1) + 1 = 5th place
        assertEquals("W", winner.getMatchHistory());
        assertEquals("L", loser.getMatchHistory());
    }

    @Test
    void updateRankings_SingleElimination_SecondRound() {
        // Arrange
        tournament.setTournamentStyle(Styles.RANDOM.getDisplayName());
        tournament.setRoundNumber(2);
        
        // Create 8-player tournament (3 rounds total)
        Set<Player> players = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            Player p = new Player("player" + i, "pass", "p" + i + "@test.com", "ROLE_USER");
            players.add(p);
        }
        tournament.setRegisteredPlayers(players);
        
        // Add rankings for match players
        RandomRanking p1Ranking = new RandomRanking(tournament, player1);
        p1Ranking.addWin(); // Won first round
        RandomRanking p2Ranking = new RandomRanking(tournament, player2);
        p2Ranking.addWin(); // Won first round
        rankings.add(p1Ranking);
        rankings.add(p2Ranking);
        tournament.setRankings(rankings);
        
        match.setWinner(player1);
        match.setDraw(false);

        // Act
        List<Ranking> updatedRankings = rankingService.updateRankings(tournament, match);

        // Assert
        RandomRanking winner = (RandomRanking) updatedRankings.get(0);
        RandomRanking loser = (RandomRanking) updatedRankings.get(1);
        assertEquals(1, winner.getRank());    // Winner continues
        assertEquals(3, loser.getRank());     // 2^(3-2) + 1 = 3rd place
        assertEquals("WW", winner.getMatchHistory());
        assertEquals("WL", loser.getMatchHistory());
    }

    @Test
    void updateRankings_SingleElimination_FinalRound() {
        // Arrange
        tournament.setTournamentStyle(Styles.RANDOM.getDisplayName());
        tournament.setRoundNumber(3);  // Final round
        
        // Create 8-player tournament
        Set<Player> players = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            Player p = new Player("player" + i, "pass", "p" + i + "@test.com", "ROLE_USER");
            players.add(p);
        }
        tournament.setRegisteredPlayers(players);
        
        // Add rankings for finalists
        RandomRanking p1Ranking = new RandomRanking(tournament, player1);
        p1Ranking.addWin(); // Won first round
        p1Ranking.addWin(); // Won second round
        RandomRanking p2Ranking = new RandomRanking(tournament, player2);
        p2Ranking.addWin(); // Won first round
        p2Ranking.addWin(); // Won second round
        rankings.add(p1Ranking);
        rankings.add(p2Ranking);
        tournament.setRankings(rankings);
        
        match.setWinner(player1);
        match.setDraw(false);

        // Act
        List<Ranking> updatedRankings = rankingService.updateRankings(tournament, match);

        // Assert
        RandomRanking winner = (RandomRanking) updatedRankings.get(0);
        RandomRanking loser = (RandomRanking) updatedRankings.get(1);
        assertEquals(1, winner.getRank());    // Tournament winner
        assertEquals(2, loser.getRank());     // Runner-up
        assertEquals("WWW", winner.getMatchHistory());
        assertEquals("WWL", loser.getMatchHistory());
    }

    @Test
    void updateRankings_RoundRobin_MultipleMatches() {
        // Arrange
        tournament.setTournamentStyle(Styles.ROUND_ROBIN.getDisplayName());
        Player player3 = new Player("player3", "pass", "p3@test.com", "ROLE_USER");
        
        rankings.add(new RoundRobinRanking(tournament, player1));
        rankings.add(new RoundRobinRanking(tournament, player2));
        rankings.add(new RoundRobinRanking(tournament, player3));
        tournament.setRankings(rankings);
        
        // Simulate multiple matches
        match.setWinner(player1);  // Player1 wins first match
        rankingService.updateRankings(tournament, match);
        
        Match match2 = new Match();
        match2.setPlayer1(player2);
        match2.setPlayer2(player3);
        match2.setTournament(tournament);
        match2.setWinner(player2);  // Player2 wins second match
        
        // Act
        List<Ranking> updatedRankings = rankingService.updateRankings(tournament, match2);

        // Assert
        assertEquals(3, updatedRankings.size());
        RoundRobinRanking p1Ranking = (RoundRobinRanking) updatedRankings.get(0);
        RoundRobinRanking p2Ranking = (RoundRobinRanking) updatedRankings.get(1);
        RoundRobinRanking p3Ranking = (RoundRobinRanking) updatedRankings.get(2);
        
        assertEquals(3, p1Ranking.getPoints());  // One win
        assertEquals(3, p2Ranking.getPoints());  // One win
        assertEquals(0, p3Ranking.getPoints());  // One loss
        assertEquals(1, p1Ranking.getRank());
        assertEquals(1, p2Ranking.getRank());   // Same rank due to both winning one match
        assertEquals(3, p3Ranking.getRank());
    }


    @Test
    void updateRankings_RoundRobin_TwoRounds() {
        // Arrange
        tournament.setTournamentStyle(Styles.ROUND_ROBIN.getDisplayName());
        Player player3 = new Player("player3", "pass", "p3@test.com", "ROLE_USER");
        
        rankings.add(new RoundRobinRanking(tournament, player1));
        rankings.add(new RoundRobinRanking(tournament, player2));
        rankings.add(new RoundRobinRanking(tournament, player3));
        tournament.setRankings(rankings);
        
        // Round 1: player1 vs player2 (player1 wins)
        match.setWinner(player1);
        rankings = rankingService.updateRankings(tournament, match);
        
        // Verify after first match
        RoundRobinRanking p1RankingAfterMatch1 = (RoundRobinRanking) rankings.get(0);
        assertEquals(3, p1RankingAfterMatch1.getPoints(), "Player1 should have 3 points after winning first match");
        
        // Round 1: player2 vs player3 (player2 wins)
        Match match2 = new Match();
        match2.setPlayer1(player2);
        match2.setPlayer2(player3);
        match2.setTournament(tournament);
        match2.setWinner(player2);
        rankings = rankingService.updateRankings(tournament, match2);
        
        // Verify after second match
        RoundRobinRanking p2RankingAfterMatch2 = (RoundRobinRanking) rankings.stream()
            .filter(r -> r.getPlayer().equals(player2))
            .findFirst().get();
        assertEquals(3, p2RankingAfterMatch2.getPoints(), "Player2 should have 3 points after winning second match");
        
        // Round 1: player1 vs player3 (player1 wins)
        Match match3 = new Match();
        match3.setPlayer1(player1);
        match3.setPlayer2(player3);
        match3.setTournament(tournament);
        match3.setWinner(player1);
        rankings = rankingService.updateRankings(tournament, match3);
        
        // Verify after third match
        RoundRobinRanking p1RankingAfterMatch3 = (RoundRobinRanking) rankings.stream()
            .filter(r -> r.getPlayer().equals(player1))
            .findFirst().get();
        assertEquals(6, p1RankingAfterMatch3.getPoints(), "Player1 should have 6 points after winning second match");
        
        // Round 2: player1 vs player2 (player2 wins)
        Match match4 = new Match();
        match4.setPlayer1(player1);
        match4.setPlayer2(player2);
        match4.setTournament(tournament);
        match4.setWinner(player2);
        List<Ranking> finalRankings = rankingService.updateRankings(tournament, match4);

        // Final Assert
        assertEquals(3, finalRankings.size());
        RoundRobinRanking p1Ranking = (RoundRobinRanking) finalRankings.stream()
            .filter(r -> r.getPlayer().equals(player1))
            .findFirst().get();
        RoundRobinRanking p2Ranking = (RoundRobinRanking) finalRankings.stream()
            .filter(r -> r.getPlayer().equals(player2))
            .findFirst().get();
        RoundRobinRanking p3Ranking = (RoundRobinRanking) finalRankings.stream()
            .filter(r -> r.getPlayer().equals(player3))
            .findFirst().get();
        
        // player1: 2 wins, 1 loss = 6 points
        assertEquals(6, p1Ranking.getPoints());
        // player2: 2 wins, 1 loss = 6 points
        assertEquals(6, p2Ranking.getPoints());
        // player3: 0 wins, 3 losses = 0 points
        assertEquals(0, p3Ranking.getPoints());
        
        // Check ranks (player1 and player2 tied for first)
        assertEquals(1, p1Ranking.getRank());
        assertEquals(1, p2Ranking.getRank());
        assertEquals(3, p3Ranking.getRank());
    }
} 