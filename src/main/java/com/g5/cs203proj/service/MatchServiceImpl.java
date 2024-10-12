package com.g5.cs203proj.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.g5.cs203proj.DTO.MatchDTO;
import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.exception.MatchNotFoundException;
import com.g5.cs203proj.repository.MatchRepository;
import com.g5.cs203proj.repository.PlayerRepository;
import com.g5.cs203proj.service.PlayerService;
import com.g5.cs203proj.service.TournamentService;
import com.g5.cs203proj.exception.NotEnoughPlayersException;


/**
 * This implementation is meant for business logic, which could be added later
 * Currently, it does not have much in terms of the business logic yet
 */
@Service
public class MatchServiceImpl implements MatchService {

    private MatchRepository matchRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TournamentService tournamentService;


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

    @Override
    public void deleteMatch(Long id) {
        matchRepository.delete(findMatchById(id));
    }

    @Override
    public Match findMatchById(Long id) {
        return matchRepository.findById(id).orElse(null);
    }

    @Override
    public Match assignRandomPlayers( Long matchId ){

        // retrieve the match
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException(matchId)); // so the match needs to be created first in Postman

        Long tournamentIdOfMatch = match.getTournament().getId();

        // get the list of all available players 
        List<Player> availablePlayers = playerService.getAvailablePlayersForTournament(tournamentIdOfMatch);
        int playerCount = availablePlayers.size();
        if ( playerCount < 2 ) {
            throw new NotEnoughPlayersException(playerCount);
        }
        Collections.shuffle(availablePlayers);
        Player p1 = availablePlayers.get(0);
        Player p2 = availablePlayers.get(1);
        match.setPlayer1(p1);
        match.setPlayer2(p2);
        matchRepository.save(match);

        // then we need to add the players to match histories
        p1.addMatchesAsPlayer1(match);
        p2.addMatchesAsPlayer2(match);
        playerService.savePlayer(p1);
        playerService.savePlayer(p2);
        return match;

    }

// @Override
// public void assignPlayerToMatch(Match match, Player player) {
//     if (match.getPlayer1() == null) {
//         match.setPlayer1(player);
//     } else if (match.getPlayer2() == null) {
//         match.setPlayer2(player);
//     }
// }

    @Override
    public void processMatchResult(Match match, Player winner, boolean isDraw) {
        match.setMatchStatus("COMPLETED");
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
    public boolean bothPlayersCheckedIn(Match match) {
        if (match.getStatusP1() && match.getStatusP2()) {
            return true;
        }
        return false;
    }

    // convert Match entity to corresponding DTOs
    public MatchDTO convertToDTO(Match match) {

        MatchDTO matchDTO = new MatchDTO();

        matchDTO.setId(match.getMatchId());
        matchDTO.setPlayer1Id(match.getPlayer1() != null ? match.getPlayer1().getId() : null );
        matchDTO.setPlayer2Id(match.getPlayer2() != null ? match.getPlayer2().getId() : null );
        matchDTO.setTournamentId(match.getTournament().getId());  // Use ID instead of full object
        matchDTO.setStatusP1(match.getStatusP1());
        matchDTO.setStatusP2(match.getStatusP2());
        matchDTO.setWinnerId(match.getWinner() != null ? match.getWinner().getId() : null);
        matchDTO.setDraw(match.getDraw());
        matchDTO.setMatchStatus(match.getMatchStatus());
        matchDTO.setEloChange(match.getEloChange());

        return matchDTO;
    }

    public Match convertToEntity(MatchDTO matchDTO) {

        Match match = new Match();

        Player player1 = playerService.getPlayerById(matchDTO.getPlayer1Id());
        Player player2 = playerService.getPlayerById(matchDTO.getPlayer2Id());
        Tournament tournament = tournamentService.getTournamentById(matchDTO.getTournamentId());
    
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setTournament(tournament);
        match.setStatusP1(matchDTO.isStatusP1());
        match.setStatusP2(matchDTO.isStatusP2());
        match.setWinner(matchDTO.getWinnerId() != null ? playerService.getPlayerById(matchDTO.getWinnerId()) : null);
        match.setDraw(matchDTO.isDraw());
        match.setMatchStatus(matchDTO.getMatchStatus());    
        match.setOnlyEloChange(matchDTO.getEloChange());
    
        return match;
    }


}

