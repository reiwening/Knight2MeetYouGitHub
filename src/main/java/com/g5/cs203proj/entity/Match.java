package com.g5.cs203proj.entity;

import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.service.*;
import java.util.List;
import com.g5.cs203proj.service.*;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.*;

@Entity
public class Match {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private boolean isComplete;
    private Double eloChange;

    // Default constructor
    public Match() {

    }

    // for testing get matches of a tournament
    public Match(Long id, Player player1, Player player2) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.eloChange = eloChange;
    }

    // Other constructors, getters, setters, etc.
    public Match(Long id, Player player1, Player player2) {
        this.id = id;
        this.tournament = null;
        this.player1 = player1;
        this.player2 = player2;

        this.statusP1 = false;
        this.statusP2 = false;

        this.winner = null;
        this.isDraw = false;  // Initialize as not a draw
        this.isComplete = false;
        this.eloChange = null;
    }

    public Match(Long id, Tournament tournament) {
        this.id = id;
        this.tournament = tournament;
        this.player1 = null;
        this.player2 = null;

        this.statusP1 = false;
        this.statusP2 = false;

        this.winner = null;
        this.isDraw = false;  // Initialize as not a draw
        this.isComplete = false;
        this.eloChange = null;
    }

    public Match(Long id, Tournament tournament, Player player1, Player player2) {
        this.id = id;
        this.tournament = tournament;
        this.player1 = player1;
        this.player2 = player2;

        this.statusP1 = false;
        this.statusP2 = false;

        this.winner = null;
        this.isDraw = false;  // Initialize as not a draw
        this.isComplete = false;
        this.eloChange = null;
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
        // System.out.println("Player 1 Status: Checked-In");
        this.statusP1 = status;
    }

    public void setStatusP2(boolean status) {
        // System.out.println("Player 2 Status: Checked-In");
        this.statusP2 = status;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void setDraw(boolean isDraw) {
        this.isDraw = isDraw;
    }

    public void setIsCompleteStatus(boolean status) {
        this.isComplete = status;
    }

    // 27/6/24: method invoked on player class for now, dk if using PlayerController
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

    public boolean getIsCompleteStatus() {
        return isComplete;
    }
    
    public Double getEloChange() {
        return eloChange;
    }
}