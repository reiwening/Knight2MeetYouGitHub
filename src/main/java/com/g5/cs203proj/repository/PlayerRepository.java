package com.g5.cs203proj.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.g5.cs203proj.entity.Player;
import java.util.Optional;


@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // define a derived query to find user by username
    Optional<Player> findByUsername(String username);

    // Find all players signed up for a specific tournament and not in an ongoing match
    @Query("SELECT p FROM Player p " +
           "JOIN p.tournamentRegistered t " +
           "WHERE t.id = :tournamentId " + // this filters players to only those who have signed up for the specific tournament identified by tournamentId
           "AND NOT EXISTS (" +
           "  SELECT m FROM Match m WHERE " +
           "  (m.player1 = p OR m.player2 = p) " +
           "  AND m.matchStatus = 'IN_PROGRESS'" + // checks if either player 1 or player 2 is in a match that is ON_GOING
           ")")
    List<Player> findAllByTournamentIdAndNotInOngoingMatch(@Param("tournamentId") Long tournamentId);
    
}
