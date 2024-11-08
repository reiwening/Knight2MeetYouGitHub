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
     * calling different methods for ranking details
     * @param: tournament: tournament object
     * @param: matches: list of matches in the most recent round
     * @return: the updated and sorted list of rankings, and the tournament object is updated 
     *          with the ranking. Handles same points(round robin) or placement(random) by having 
     *          same rank
     */
    //TODO: potential optimisation, instead of taking whole ranking just take the top matches.size()
    // rankings, since those at the bottom lose alr
    public List<Ranking> updateRankings(Tournament tournament, List<Match> matches){
        List<Ranking> rankings = tournament.getRankings();
        if (tournament.getTournamentStyle().equals(Styles.ROUND_ROBIN.getDisplayName())){
            rankings = updateRoundRobinRankings(rankings, matches);
        }
        else if (tournament.getTournamentStyle().equals(Styles.RANDOM.getDisplayName())){
            rankings = updateRandomRankings(rankings, matches);
        }

        //update the tournament
        tournament.setRankings(rankings);
        return rankings;

    }
    
    /*
     * updates ranking for round robin games, orders based on points. 
     */
    private List<Ranking> updateRoundRobinRankings(List<Ranking> rankings, List<Match> matches){
        for (Match match : matches){
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
    private List<Ranking> updateRandomRankings(List<Ranking> rankings, List<Match> matches){
        //the ranks of all the losers will be winners + 1, since the winners will be the top (#winners) players
        int loserRank = matches.size() + 1;
        
        for (Match match : matches){
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
        }

        //sort ranking by rank
        rankings.sort(Comparator.comparingInt(Ranking::getRank));
        
        return rankings;
    }

    /*
     * given a player, returns the corresponding ranking from a list of rankings from the tournament
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


}
