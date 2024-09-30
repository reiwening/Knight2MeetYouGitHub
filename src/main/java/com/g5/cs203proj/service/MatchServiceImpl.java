package com.g5.cs203proj.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
import org.springframework.stereotype.Service;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.repository.MatchRepository;
import com.g5.cs203proj.repository.PlayerRepository;


/**
 * This implementation is meant for business logic, which could be added later
 * Currently, it does not have much in terms of the business logic yet
 */
@Service
public class MatchServiceImpl implements MatchService {

    private MatchRepository matchRepository;

    // constructor 
    public MatchServiceImpl( MatchRepository matchRepository ) {
        this.matchRepository = matchRepository;
    }


    /* Methods */


    // createMatch returns (long) matchId of the newly created match
    // @Override
    // public long createMatch(Tournament tournament) {
    //     Match newMatch = new Match(tournament);
    //     return newMatch.getMatchId();
    // }

    // @Override
    // public long createMatch(Tournament tournament, Player p1, Player p2) {
    //     Match newMatch = new Match(tournament, p1, p2);
    //     return newMatch.getMatchId();
    // }
    // create a match instance and save to database


    @Override
    public Match saveMatch( Match match ) {
        return matchRepository.save(match);
    }

    // @Override
    // public void deleteMatch(Long id) {
    //     matchRepository.delete(findMatchById(id));
    // }

    @Override
    public Match findMatchById(Long id) {
        return matchRepository.findById(id).orElse(null);
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



    // Need import the Player & Tournament packages to call their functions
    @Override
    public List<Match> getMatchesForTournament(Tournament tournament) {
        // TODO Auto-generated method stub
        return null;
    }

    // Need import the Player & Tournament packages to call their functions
    @Override
    public List<Match> getMatchesForPlayer(Player player) {
        // TODO Auto-generated method stub
        return null;
    }

    // Returns true if notification sent successfully
    @Override
    public boolean sendMatchStartNotification() {
        // TODO Auto-generated method stub
        return false;
    }

    // View check-in status for both players for a match
    @Override
    public boolean bothPlayersCheckedIn(Match match) {
        if (match.getStatusP1() && match.getStatusP2()) {
            return true;
        }
        return false;
    }
}