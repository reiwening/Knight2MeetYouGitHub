package com.g5.cs203proj.entity;

import com.g5.Knight2MeetYou.entity.Player;
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

}
