package com.g5.cs203proj.service;

import java.util.*;

public interface MatchService {
    // createMatch returns (long) id of the newly created match
    long createMatch();
    long createMatch();

    void assignPlayersToMatch();

    void processMatchResult();

    Match findMatchById(long matchId);

    List<Match> getMatchesForTournament(Tournament tournament);

    List<Match> getMatchesForPlayer(Player player);

    // Returns true if notification sent successfully
    boolean sendMatchStartNotification();

    // View check-in status for both players for a match
    boolean bothPlayersCheckedIn(Match match);

}
