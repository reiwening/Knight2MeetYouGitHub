package com.g5.cs203proj.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RANDOM")
public class RandomRanking extends Ranking {
    private StringBuilder matchHistory;

    public RandomRanking() {
        super();
        matchHistory = new StringBuilder();
    }
    
    public RandomRanking(Tournament tournament, Player player) {
        super(tournament, player);
        this.matchHistory = new StringBuilder();
    }

    @Override
    public void addWin() {
        matchHistory.append("W");
    }

    @Override
    public void addLoss() {
        matchHistory.append("L");
    }

    public String getMatchHistory() {
        return matchHistory.toString();
    }

    @Override
    public String toString() {
        return getPlayer().getUsername() + "[" + matchHistory.toString() + "]";
    }
}