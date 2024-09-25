package com.g5.cs203proj.entity;

import com.g5.cs203proj.entity.Match;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;



@Entity
public class Player extends User {


    private double globalEloRating;

    @ManyToMany 
    @JoinTable(
        name = "player_tournament", 
        joinColumns = @JoinColumn(name = "player_id"), 
        inverseJoinColumns = @JoinColumn(name = "tournament_id"))
    private List<Tournament> tournamentRegistered;

    @OneToMany(mappedBy = "player1")
    private List<Match> matchesAsPlayer1;

    @OneToMany(mappedBy = "player2")
    private List<Match> matchesAsPlayer2;

    @Transient // This field is not persisted directly, but computed
    private List<Match> matchHistory;

    public Player(String username, Long id, String hashedPassword, double globalEloRating) {
        super(username, id, hashedPassword);
        this.globalEloRating = globalEloRating;
    }

    public Player() {
        
    }
    

    // Getter for matchHistory which consolidates both lists
    public List<Match> getMatchHistory() {
        matchHistory = new ArrayList<>();
        matchHistory.addAll(matchesAsPlayer1);
        matchHistory.addAll(matchesAsPlayer2);
        return matchHistory;
    }

    
}
