package com.g5.cs203proj.DTO;

import com.g5.cs203proj.entity.Player;

public class MatchDTO {
    
    private Long id;  
    private Long player1Id;
    private Long player2Id;
    private Long tournamentId;
    private boolean statusP1;
    private boolean statusP2;
    private Long winnerId;
    private boolean isDraw;
    private String matchStatus = "NOT_STARTED";
    private Double eloChange;
    
    public MatchDTO(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public boolean isStatusP1() {
        return statusP1;
    }

    public void setStatusP1(boolean statusP1) {
        this.statusP1 = statusP1;
    }

    public boolean isStatusP2() {
        return statusP2;
    }

    public void setStatusP2(boolean statusP2) {
        this.statusP2 = statusP2;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public void setDraw(boolean isDraw) {
        this.isDraw = isDraw;
    }

    public Double getEloChange() {
        return eloChange;
    }

    public void setEloChange(Double eloChange) {
        this.eloChange = eloChange;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }    
    
    
    
}
