package com.g5.cs203proj.service;

import java.util.*;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.DTO.MatchDTO;
import com.g5.cs203proj.entity.Match;

public interface MatchService {

    Match saveMatch(Match match);
    void deleteMatch(Long id);
    Match findMatchById(Long id);
    
    Match assignRandomPlayers(Long MatchId);
    Match reassignPlayersToMatch(Long oldMatchId, Long newMatchId);
    List<Match> createRoundRobinMatches(Long tournamentId);
    List<Match> createSingleEliminationMatches(Long tournamentId);
    void processMatchResult(Match match, Player winner, boolean isDraw);


    List<Match> getMatchesForTournament(Tournament tournament);
    List<Match> getMatchesForPlayer(Player player);

    // View check-in status for both players for a match
    HashMap<String, Boolean> viewCheckedInStatus(Match match);

    MatchDTO convertToDTO(Match match);
    Match convertToEntity(MatchDTO matchDTO);

    
}
