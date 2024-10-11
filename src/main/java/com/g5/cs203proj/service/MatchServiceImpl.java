package com.g5.cs203proj.service;

import java.util.HashMap;
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
    public void assignPlayerToMatch(Match match, Player player) {
        if (match.getPlayer1() == null) {
            match.setPlayer1(player);
        } else if (match.getPlayer2() == null) {
            match.setPlayer2(player);
        }
    }

    @Override
    public void processMatchResult(Match match, Player winner, boolean isDraw) {
        match.setIsCompleteStatus(true);
        match.setDraw(isDraw);

        if (isDraw) {
            match.setWinner(null);  // No winner in case of a draw
        } else {
            match.setWinner(winner);
        }
        
        // Elo change uses isDraw attribute from Match. If draw, will auto calculate
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
    public HashMap<String, Boolean> viewCheckedInStatus(Match match) {
        HashMap<String, Boolean> checkInStatuses = new HashMap<>();

        String p1 = match.getPlayer1().getUsername();
        String p2 = match.getPlayer2().getUsername();

        Boolean p1Status = match.getStatusP1();
        Boolean p2Status = match.getStatusP2();

        checkInStatuses.put(p1, p1Status);
        checkInStatuses.put(p2, p2Status);

        return checkInStatuses;
    }
}

