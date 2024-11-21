package com.g5.cs203proj.entity;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "ranking_type")
public abstract class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @ManyToOne
    protected Player player;
    
    @ManyToOne
    protected Tournament tournament;
    
    protected int rank;

    // Default constructor
    protected Ranking() {
        rank = 1;
    }
    
    // Constructor with player and tournament
    protected Ranking(Tournament tournament, Player player) {
        this.tournament = tournament;
        this.player = player;
        rank = 1;
    }

    // Common methods
    public Player getPlayer() {
        return player;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Long getId() {
        return id;
    }

    // Abstract methods that child classes must implement
    public abstract void addWin();
    public abstract void addLoss();
    public abstract String toString();
}