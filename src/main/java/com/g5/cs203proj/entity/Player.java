package com.g5.cs203proj.entity;

import com.g5.cs203proj.entity.Match;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;



@Entity
public class Player {

    private String username;
    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;

    private String hashedPassword;
    private double globalEloRating;

    @ManyToMany 
    @JsonIgnore
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

    

    public Player(String username, Long id, String hashedPassword) {
        this.username = username;
        this.id = id;
        this.hashedPassword = hashedPassword;
    }


    public Player() {
        
    }
    

    // Getter for matchHistory which consolidates both lists
    // Getter for matchHistory which consolidates both lists
    public List<Match> getMatchHistory() {
        if (matchHistory == null) {
            matchHistory = new ArrayList<>();  // Initialize the list if it's null
        }
        matchHistory.clear();  // Clear the list to ensure no duplicate entries
        if (matchesAsPlayer1 != null) {
            matchHistory.addAll(matchesAsPlayer1);
        }
        if (matchesAsPlayer2 != null) {
            matchHistory.addAll(matchesAsPlayer2);
        }
        return matchHistory;
    }

// public List<Match> getMatchHistory() {
//     matchHistory = new ArrayList<>();
//     matchHistory.addAll(matchesAsPlayer1);
//     matchHistory.addAll(matchesAsPlayer2);
//     return matchHistory;
// }

    public String getUsername() {
        return username;
    }

    public double getGlobalEloRating() {
        return globalEloRating;
    }


    public void setGlobalEloRating(double globalEloRating) {
        this.globalEloRating = globalEloRating;
    }

    
    public List<Tournament> getTournamentRegistered() {
        return tournamentRegistered;
    }


    public void setTournamentRegistered(List<Tournament> tournamentRegistered) {
        this.tournamentRegistered = tournamentRegistered;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    
}
