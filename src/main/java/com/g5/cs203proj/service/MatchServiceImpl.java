package com.g5.cs203proj.service;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


/**
 * This implementation is meant for business logic, which could be added later
 * Currently, it does not have much in terms of the business logic yet
 */
@Service
public class MatchServiceImpl implements MatchService {
    // createMatch returns (long) matchId of the newly created match
    @Override
    public long createMatch(Tournament tournament) {
        Match newMatch = new Match(tournament);
        return newMatch.getMatchId();
    }

    @Override
    public long createMatch(Tournament tournament, Player p1, Player p2) {
        Match newMatch = new Match(tournament, p1, p2);
        return newMatch.getMatchId();
    }

    @Override
    public void assignPlayersToMatch(Match match, Player p1, Player p2) {
        match.setPlayer1(p1);
        match.setPlayer2(p2);
    }

    @Override
    public void processMatchResult(Match match, Player winner) {
        match.setIsCompleteStatus(true);
        match.setWinner(winner);
        match.setEloChange(winner);
    }


    // Need import the Player & Tournament packages to find from their history lists
    @Override
    public Match findMatchById(long matchId) {
        
    };

    // Need import the Player & Tournament packages to call their functions
    @Override
    public List<Match> getMatchesForTournament(Tournament tournament) {

    };

    // Need import the Player & Tournament packages to call their functions
    @Override
    public List<Match> getMatchesForPlayer(Player player) {

    };

    // Returns true if notification sent successfully
    @Override
    public boolean sendMatchStartNotification() {

    };

    // View check-in status for both players for a match
    @Override
    public boolean bothPlayersCheckedIn(Match match) {
        if (match.getStatusP1() && match.getStatusP2()) {
            return true;
        }
        return false;
    };
}
