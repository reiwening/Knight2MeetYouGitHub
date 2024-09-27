package com.g5.cs203proj.entity;

import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.service.*;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

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
    
    private boolean isComplete;
    private int eloChange;

    public Match(Tournament tournament) {
        this.id = null;
        this.tournament = tournament;
        this.player1 = null;
        this.player2 = null;

        this.statusP1 = false;
        this.statusP2 = false;

        this.winner = null;
        this.isComplete = false;
        this.eloChange = null;
    }

    public Match(Tournament tournament, Player player1, Player player2) {
        this.id = null;
        this.tournament = tournament;
        this.player1 = player1;
        this.player2 = player2;

        this.statusP1 = false;
        this.statusP2 = false;

        this.winner = null;
        this.isComplete = false;
        this.eloChange = null;
    }


    // Setter methods
    public void setMatchId(long matchId) {
        this.id = matchId;
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

    public void setStatusP1() {
        System.out.println("Player 1 Status: Checked-In");
        this.statusP1 = true;
    }
    
    public void setStatusP2() {
        System.out.println("Player 2 Status: Checked-In");
        this.statusP2 = true;
    }
    
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void setIsCompleteStatus(boolean status) {
        this.isComplete = status;
    }

    // 27/6/24: method invoked on player class for now, dk if using PlayerController
    public void setEloChange(Player winner) {
        // Some elo change calculation
        double change = 10;

        if (player1 == winner) {
            player1.setPlayerGlobalEloRating(change);
            player2.setPlayerGlobalEloRating(change * -1);
        } else if (player2 == winner) {
            player2.setPlayerGlobalEloRating(change);
            player1.setPlayerGlobalEloRating(change * -1);
        } else {
            player1.setPlayerGlobalEloRating(change);
            player2.setPlayerGlobalEloRating(change);
        }
    }

    



    // Getter methods
    public long getMatchId() {
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

    public boolean getIsCompleteStatus() {
        return isComplete;
    }
    
    // 16/9/24: Later do 
    public double getEloChange() {
        return eloChange;
    }

}
