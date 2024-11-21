package com.g5.cs203proj;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.enums.*;
import com.g5.cs203proj.service.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TournamentLifecycleIntegrationTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private PlayerService playerService;

    private Tournament tournament;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        // Create players with specific Elo ratings for predictable results
        players = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Player player = new Player(
                "player" + i,
                "password123",
                "player" + i + "@test.com",
                "ROLE_USER"
            );
            player.setGlobalEloRating(1500 + (i * 100)); // Clear Elo differences
            System.out.println("Saved player with ID: " + player.getId());
            players.add(playerService.savePlayer(player));
        }
    }

    @AfterEach
    void cleanup() {
        if (tournament != null) {
            // Clear matches first
            tournament.getTournamentMatchHistory().clear();
            tournament.getRankings().clear();
            tournamentService.updateTournament(tournament.getId(), tournament);
            
            // Then delete tournament
            tournamentService.deleteTournament(tournament.getId());
        }
        
        // Finally delete players
        for (Player player : players) {
            try {
                playerService.deletePlayer(player.getUsername());
            } catch (Exception e) {
                // Log the error but continue with other deletions
                System.err.println("Error deleting player: " + player.getUsername());
            }
        }
    }

    // @Test
    // @Transactional
    // void simulateSingleEliminationTournament() {
    //     // Create tournament for this test
    //     tournament = new Tournament();
    //     tournament.setName("Single Elimination Test Tournament");
    //     tournament.setTournamentStyle(Styles.RANDOM.getDisplayName());
    //     tournament.setMinPlayers(4);
    //     tournament.setMaxPlayers(8);
    //     tournament.setMinElo(1000);
    //     tournament.setMaxElo(2500);
    //     tournament.setRegistrationCutOff(LocalDateTime.now().plusDays(1));
    //     tournament.setTournamentStatus(Statuses.REGISTRATION.getDisplayName());
    //     tournament.setRegisteredPlayers(new HashSet<>());
    //     tournament.setTournamentMatchHistory(new ArrayList<>());
    //     tournament.setRoundNumber(1);
        
    //     tournament = tournamentService.createTournament(tournament);
    //     System.out.println("Created Single Elimination tournament with ID: " + tournament.getId());

    //     // Register players
    //     for (Player player : players) {
    //         tournamentService.registerPlayer(player.getId(), tournament.getId());
    //     }
        
    //     tournament = tournamentService.startOrCancelTournament(tournament.getId());
        
    //     // First round - Players 8,7,6,5 should win due to higher Elo
    //     List<Match> firstRoundMatches = matchService.createSingleEliminationMatches(tournament.getId());
    //     assertEquals(7, firstRoundMatches.size());
    //     // Verify only first 4 matches are active for round 1
    //     int activeMatches = 0;
    //     for (Match match : firstRoundMatches) {
    //         if (match.getPlayer1() != null && match.getPlayer2() != null) {
    //             activeMatches++;
    //         }
    //     }
    //     assertEquals(4, activeMatches);
        
    //     // Simulate only the active matches
    //     List<Match> activeFirstRoundMatches = firstRoundMatches.stream()
    //         .filter(m -> m.getPlayer1() != null && m.getPlayer2() != null)
    //         .collect(Collectors.toList());
    //     simulateAndVerifyRound(activeFirstRoundMatches, 
    //             Arrays.asList(players.get(7), players.get(6), players.get(5), players.get(4)));
    //     verifyEliminatedPlayers(Arrays.asList(players.get(0), players.get(1), players.get(2), players.get(3)), 5);
        
    //     // Semifinals - Players 8,7 should win
    //     // For subsequent rounds, only process matches with assigned players
    //     List<Match> semifinals = tournamentService.processSingleEliminationRound(tournament.getId())
    //         .stream()
    //         .filter(m -> m.getPlayer1() != null && m.getPlayer2() != null)
    //         .collect(Collectors.toList());
    //     assertEquals(2, semifinals.size());
    //     simulateAndVerifyRound(semifinals, Arrays.asList(players.get(7), players.get(6)));
    //     verifyEliminatedPlayers(Arrays.asList(players.get(4), players.get(5)), 3);
        
    //     // Finals - Player 8 should win
    //     List<Match> finals = tournamentService.processSingleEliminationRound(tournament.getId())
    //         .stream()
    //         .filter(m -> m.getPlayer1() != null && m.getPlayer2() != null)
    //         .collect(Collectors.toList());
    //     assertEquals(1, finals.size());
    //     simulateAndVerifyRound(finals, Arrays.asList(players.get(7)));
    //     verifyEliminatedPlayers(Arrays.asList(players.get(6)), 2);

    //     // Verify final rankings
    //     tournament = tournamentService.getTournamentById(tournament.getId());
    //     List<Ranking> finalRankings = tournament.getRankings();
    //     Optional<Ranking> winnerRanking = finalRankings.stream()
    //         .filter(r -> r.getPlayer().equals(players.get(7)))
    //         .findFirst();
        
    //     assertTrue(winnerRanking.isPresent(), "Winner ranking should be present");
    //     RandomRanking winner = (RandomRanking) winnerRanking.get();
    //     assertEquals("WWW", winner.getMatchHistory());
    //     assertEquals(1, winner.getRank());
    // }

    private void simulateAndVerifyRound(List<Match> matches, List<Player> expectedWinners) {
        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            Player winner = expectedWinners.get(i);
            matchService.processMatchResult(match, winner, false);
            // Flush changes to ensure rankings are updated
            entityManager.flush();
            entityManager.clear();
        
            // Refresh tournament to get updated rankings
            tournament = tournamentService.getTournamentById(tournament.getId());
        }
    }

    private void verifyEliminatedPlayers(List<Player> eliminatedPlayers, int expectedRank) {
        // Refresh tournament to ensure we have latest rankings
        tournament = tournamentService.getTournamentById(tournament.getId());
        for (Player player : eliminatedPlayers) {
            Optional<Ranking> rankingOpt = tournament.getRankings().stream()
                .filter(r -> r.getPlayer().equals(player))
                .findFirst();
            
            assertTrue(rankingOpt.isPresent(), 
                "Ranking should exist for eliminated player: " + player.getUsername());
            
            RandomRanking ranking = (RandomRanking) rankingOpt.get();
            assertEquals(expectedRank, ranking.getRank(), 
                "Player " + player.getUsername() + " should have rank " + expectedRank);
        }
    }

    @Test
    @Transactional
    void simulateRoundRobinTournament() {
        // Create tournament for this test
        tournament = new Tournament();
        tournament.setName("Round Robin Test Tournament");
        tournament.setTournamentStyle(Styles.ROUND_ROBIN.getDisplayName());
        tournament.setMinPlayers(4);
        tournament.setMaxPlayers(8);
        tournament.setMinElo(1000);
        tournament.setMaxElo(2500);
        tournament.setRegistrationCutOff(LocalDateTime.now().plusDays(1));
        tournament.setTournamentStatus(Statuses.REGISTRATION.getDisplayName());
        tournament.setRegisteredPlayers(new HashSet<>());
        tournament.setTournamentMatchHistory(new ArrayList<>());
        tournament.setRoundNumber(1);
        
        tournament = tournamentService.createTournament(tournament);
        System.out.println("Created Round Robin tournament with ID: " + tournament.getId());

        // Register first 4 players only
        List<Player> roundRobinPlayers = players.subList(0, 4);
        for (Player player : roundRobinPlayers) {
            tournamentService.registerPlayer(player.getId(), tournament.getId());
        }
        
        tournament = tournamentService.startOrCancelTournament(tournament.getId());
        assertEquals(Statuses.IN_PROGRESS.getDisplayName(), tournament.getTournamentStatus());
        
        // In round robin, each player plays against every other player
        List<Match> allMatches = matchService.createRoundRobinMatches(tournament.getId());
        
        // Expected number of matches = n(n-1)/2 where n is number of players
        assertEquals(6, allMatches.size());  // 4 players = 6 matches

        // Simulate specific results:
        // Player4 wins all matches (9 points)
        // Player3 wins against Player2 and Player1 (6 points)
        // Player2 wins against Player1 (3 points)
        // Player1 loses all matches (0 points)
        
        Map<String, Integer> expectedPoints = new HashMap<>();
        expectedPoints.put(roundRobinPlayers.get(3).getUsername(), 9); // Player4
        expectedPoints.put(roundRobinPlayers.get(2).getUsername(), 6); // Player3
        expectedPoints.put(roundRobinPlayers.get(1).getUsername(), 3); // Player2
        expectedPoints.put(roundRobinPlayers.get(0).getUsername(), 0); // Player1

        for (Match match : allMatches) {
            Player player1 = match.getPlayer1();
            Player player2 = match.getPlayer2();
            
            // Determine winner based on our expected results
            Player winner;
            if (player1.getUsername().equals("player4") || player2.getUsername().equals("player4")) {
                winner = player1.getUsername().equals("player4") ? player1 : player2;
            } else if (player1.getUsername().equals("player3") || player2.getUsername().equals("player3")) {
                winner = player1.getUsername().equals("player3") ? player1 : player2;
            } else {
                winner = player1.getUsername().equals("player2") ? player1 : player2;
            }
            
            matchService.processMatchResult(match, winner, false);
        }

        // Verify final rankings
        tournament = tournamentService.getTournamentById(tournament.getId());
        List<Ranking> finalRankings = tournament.getRankings();
        assertEquals(4, finalRankings.size());

        // Verify points and ranks
        for (Ranking ranking : finalRankings) {
            RoundRobinRanking roundRobinRanking = (RoundRobinRanking) ranking;
            String playerUsername = roundRobinRanking.getPlayer().getUsername();
            int expectedPoint = expectedPoints.get(playerUsername);
            assertEquals(expectedPoint, roundRobinRanking.getPoints(), 
                "Wrong points for " + playerUsername);
        }

        // Verify ranks (1st to 4th)
        RoundRobinRanking firstPlace = (RoundRobinRanking) finalRankings.get(0);
        RoundRobinRanking secondPlace = (RoundRobinRanking) finalRankings.get(1);
        RoundRobinRanking thirdPlace = (RoundRobinRanking) finalRankings.get(2);
        RoundRobinRanking fourthPlace = (RoundRobinRanking) finalRankings.get(3);

        assertEquals("player4", firstPlace.getPlayer().getUsername());
        assertEquals(1, firstPlace.getRank());
        assertEquals(9, firstPlace.getPoints());

        assertEquals("player3", secondPlace.getPlayer().getUsername());
        assertEquals(2, secondPlace.getRank());
        assertEquals(6, secondPlace.getPoints());

        assertEquals("player2", thirdPlace.getPlayer().getUsername());
        assertEquals(3, thirdPlace.getRank());
        assertEquals(3, thirdPlace.getPoints());

        assertEquals("player1", fourthPlace.getPlayer().getUsername());
        assertEquals(4, fourthPlace.getRank());
        assertEquals(0, fourthPlace.getPoints());
    }
}
