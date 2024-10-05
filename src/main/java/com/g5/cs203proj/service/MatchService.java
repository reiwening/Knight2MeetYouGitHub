package com.g5.cs203proj.service;

import java.util.*;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.entity.Match;

public interface MatchService {

    // createMatch returns (long) id of the newly created match
    // long createMatch();

    Match saveMatch(Match match);
    // Match deleteMatch(Long id);
    Match findMatchById(Long id);

    void assignPlayersToMatch(Match match, Player p1, Player p2);
    void processMatchResult(Match match, Player winner);

    List<Match> getMatchesForTournament(Tournament tournament);
    List<Match> getMatchesForPlayer(Player player);

    // Returns true if notification sent successfully
    boolean sendMatchStartNotification();

    // View check-in status for both players for a match
    boolean bothPlayersCheckedIn(Match match);

}