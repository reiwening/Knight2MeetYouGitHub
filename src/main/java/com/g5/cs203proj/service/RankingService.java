package com.g5.cs203proj.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.g5.cs203proj.entity.Match;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.entity.RandomRanking;
import com.g5.cs203proj.entity.Ranking;
import com.g5.cs203proj.entity.RoundRobinRanking;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.enums.Styles;
import com.g5.cs203proj.exception.tournament.RankingNotFound;

@Service
public class RankingService {

    public RankingService(){};

    /*
     * updates tournament rankings based on the most recent round of matches. rankingService handles
     * different tournament styles, and updates in tournament. Determines tournament type before
     * calling different methods for ranking details. Does not update tournament object.
     * @param: tournament: tournament object
     * @param: match: match object which affects rankings
     * @return: the updated and sorted list of rankings, and the tournament object is updated 
     *          with the ranking. Handles same points(round robin) or placement(random) by having 
     *          same rank
    */
    public List<Ranking> updateRankings(Tournament tournament, Match match){
        List<Ranking> rankings = tournament.getRankings();
        if (tournament.getTournamentStyle().equals(Styles.ROUND_ROBIN.getDisplayName())){ 
           rankings = updateRoundRobinRankings(rankings, match);
        }
        else if (tournament.getTournamentStyle().equals(Styles.RANDOM.getDisplayName())){
            //get rank of players eliminated in this round
            int loserRank = getLoserRank(tournament);
            rankings = updateRandomRankings(rankings, match, loserRank);
        }

        return rankings;

    }
    
    /*
     * updates ranking for round robin games, orders based on points. 
     */
    private List<Ranking> updateRoundRobinRankings(List<Ranking> rankings, Match match){
        RoundRobinRanking player1Ranking = (RoundRobinRanking)getRankingByPlayer(rankings, match.getPlayer1());
        RoundRobinRanking player2Ranking = (RoundRobinRanking)getRankingByPlayer(rankings, match.getPlayer2());

        //handle tie
        if (match.getDraw()){
            player1Ranking.addTie();
            player2Ranking.addTie();
        }
        //player 1 wins
        else if (match.getWinner().equals(match.getPlayer1())){
            player1Ranking.addWin();
            player2Ranking.addLoss();
        }
        //player 2 wins
        else{
            player2Ranking.addWin();
            player1Ranking.addLoss();
        }

        //sort rankings based on points
        rankings.sort(Comparator.comparingInt(r -> -((RoundRobinRanking) r).getPoints()));

        //update rank info in each ranking. 
        for (int i = 0; i < rankings.size(); i++){
            //1st one (index 0) will be rank 1 (i + 1)
            rankings.get(i).setRank(i + 1);
        }

        //look for points ties, handle same points by having same rank, loops from 2nd player until end
        for (int i = 1; i < rankings.size(); i++){
            RoundRobinRanking curr = (RoundRobinRanking)rankings.get(i);
            RoundRobinRanking prev = (RoundRobinRanking)rankings.get(i - 1);
            if (curr.getPoints() == prev.getPoints()){
                curr.setRank(prev.getRank());
            }
        }

        return rankings;
    }

    /*
     * updates ranking for random games, orders based on rank. Players eliminated will have rank
    */
    private List<Ranking> updateRandomRankings(List<Ranking> rankings, Match match, int loserRank){
        RandomRanking player1Ranking = (RandomRanking)getRankingByPlayer(rankings, match.getPlayer1());
        RandomRanking player2Ranking = (RandomRanking)getRankingByPlayer(rankings, match.getPlayer2());

        //player 1 wins
        if (match.getWinner().equals(match.getPlayer1())){
            player1Ranking.addWin();
            player2Ranking.addLoss();
            player2Ranking.setRank(loserRank);
        }
        //player 2 wins
        else{
            player2Ranking.addWin();
            player1Ranking.addLoss();
            player1Ranking.setRank(loserRank);
        }

        //sort ranking by rank
        rankings.sort(Comparator.comparingInt(Ranking::getRank));
        
        return rankings;
    }

    /*
     * given a player, returns the corresponding ranking object from a list of rankings from the tournament
     * @param: rankings: rankings list from tournament
     * @param: player: player to search for
     * @return: ranking object for the player for that tournament
     */
    public Ranking getRankingByPlayer(List<Ranking> rankings, Player player){
        for (Ranking ranking: rankings){
            if (ranking.getPlayer().equals(player)){
                return ranking;
            }
        }
        throw new RankingNotFound(player.getUsername());
    }

    /*
     * given a tournament, returns the rank of the players eliminated in the current round
     * @param: tournament: tournament object
     * @return: rank of the players eliminated in the current round
    */
    private int getLoserRank(Tournament tournament){
        int playerCount = tournament.getRegisteredPlayers().size();
        int round = tournament.getRoundNumber();
        // Players eliminated in round N will share rank (2^(totalRounds-N) + 1)
        int totalRounds = (int)(Math.log(playerCount) / Math.log(2));
        return (int)Math.pow(2, totalRounds - round) + 1;
    }

}
