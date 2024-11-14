package com.g5.cs203proj.entity;

// import jakarta.persistence.DiscriminatorColumn;
// import jakarta.persistence.DiscriminatorType;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Inheritance;
// import jakarta.persistence.InheritanceType;
// import jakarta.persistence.ManyToOne;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "ranking_type", discriminatorType = DiscriminatorType.STRING)
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Tournament tournament;

    @ManyToOne
    private Player player;

    private int rank; // Rank in the tournament


    public Ranking(Tournament tournament, Player player) {
        this.tournament = tournament;
        this.player = player;
        rank = 1; //default every player to rank 1 at the start
    }

    public Long getId() {
        return id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
    
}

