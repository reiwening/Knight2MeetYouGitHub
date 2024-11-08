package com.g5.cs203proj.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RoundRobin")
public class RoundRobinRanking extends Ranking{
    //points gained from winning
    private static final int WIN_POINTS = 3;
    //points gained from tying
    private static final int TIE_POINTS = 1;

    private int wins; // Wins for Round Robin
    private int losses; // Losses for Round Robin
    private int ties; // Ties for Round Robin
    private int points; // Points for Round Robin (Wins * 3 + Ties * 1)

    public RoundRobinRanking(Tournament tournament, Player player ){
        super(tournament, player);
        wins = 0;
        losses = 0;
        ties = 0;
        points = 0;
    }
//getters
    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTies() {
        return ties;
    }

    public int getPoints() {
        return points;
    }
    
//setters
    public void addWin() {
        wins++;
        points += WIN_POINTS;
    }

    public void addLoss() {
        losses++;
    }

    public void addTie() {
        ties++;
        points += TIE_POINTS;
    }

    @Override
    public String toString() {
        return  getPlayer().getUsername() + "[wins=" + wins + ", losses=" + losses + ", ties=" + ties + ", points=" + points + "]";
    }
    
}
