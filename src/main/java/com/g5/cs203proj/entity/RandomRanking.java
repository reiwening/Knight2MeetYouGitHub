package com.g5.cs203proj.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Random")
public class RandomRanking extends Ranking{
    private StringBuilder matchHistory; // Match history string for Random tournaments

    public RandomRanking(Tournament tournament, Player player ){
        super(tournament, player);
        matchHistory = new StringBuilder();
    }

    public String getMatchHistory(){
        return matchHistory.toString();
    }

    public void addWin(){
        matchHistory.append("W");
    }
    public void addLoss(){
        matchHistory.append("L");
    }

    @Override
    public String toString(){
        return getPlayer().getUsername() + "[" +matchHistory.toString() + "]";
    }

}
