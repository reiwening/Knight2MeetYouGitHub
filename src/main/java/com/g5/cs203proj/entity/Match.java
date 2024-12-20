package com.g5.cs203proj.entity;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Match {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "player1_id")  // Foreign key for player1
    private Player player1;

    @ManyToOne
    @JoinColumn(name = "player2_id")  // Foreign key for player2
    private Player player2;

    private boolean statusP1;
    private boolean statusP2;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Player winner;
    
    private boolean isDraw;

    private String matchStatus = "NOT_STARTED"; // "NOT_STARTED (default) | "ONGOING" | "COMPLETED"

    private Double eloChange;

    // Default constructor
    public Match() {

    }
    
    // Other constructors, getters, setters, etc.
    public Match(Long id, Player player1, Player player2) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
    }

    public Match(Tournament tournament) {  
        this.tournament = tournament;
    }

    public Match(Tournament tournament, Player player1, Player player2) {
        this.tournament = tournament;
        this.player1 = player1;
        this.player2 = player2;
    }


    // Setter methods
    public void setMatchId(Long id) {
        this.id = id;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setStatusP1(boolean status) {
        this.statusP1 = status;
    }

    public void setStatusP2(boolean status) {
        this.statusP2 = status;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void setDraw(boolean isDraw) {
        this.isDraw = isDraw;
    }

    public void setEloChange(Player winner) {
        double kFactor = 32.0;
        double ratingP1 = player1.getGlobalEloRating();
        double ratingP2 = player2.getGlobalEloRating();
    
        double expectedP1 = 1 / (1 + Math.pow(10, (ratingP2 - ratingP1) / 400));
        double expectedP2 = 1 / (1 + Math.pow(10, (ratingP1 - ratingP2) / 400));
    
        double scoreP1 = isDraw ? 0.5 : (player1 == winner ? 1 : 0);
        double scoreP2 = isDraw ? 0.5 : (player2 == winner ? 1 : 0);
    
        double newRatingP1 = Math.round(ratingP1 + kFactor * (scoreP1 - expectedP1));
        double newRatingP2 = Math.round(ratingP2 + kFactor * (scoreP2 - expectedP2));
    
        player1.setGlobalEloRating(newRatingP1);
        player2.setGlobalEloRating(newRatingP2);
    
        this.eloChange = Math.abs(newRatingP1 - ratingP1);
    }

    public void setOnlyEloChange(Double newEloChange) {
        eloChange = newEloChange;
    }
    

    // Getter methods
    public Long getMatchId() {
        return id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public boolean getStatusP1() {
        return statusP1;
    }

    public boolean getStatusP2() {
        return statusP2;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean getDraw(){
        return isDraw;
    }

    public Double getEloChange() {
        return eloChange;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }
}