package com.g5.cs203proj.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TournamentDTO {

    private Long tournamentId;
    private String name;
    private List<Long> tournamentMatchHistoryId = new ArrayList<>();  // Only include match IDs, not the full Match objects

    private String tournamentStatus;
    private String tournamentStyle = "random"; // random by default 

    private List<Long> registeredPlayersId = new ArrayList<>();

    private Map<Long, Integer> rankings;
    private int maxPlayers;
    
    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getTournamentMatchHistoryId() {
        return tournamentMatchHistoryId;
    }

    public void setTournamentMatchHistoryId(List<Long> tournamentMatchHistoryId) {
        this.tournamentMatchHistoryId = tournamentMatchHistoryId;
    }

    public String getTournamentStatus() {
        return tournamentStatus;
    }

    public void setTournamentStatus(String tournamentStatus) {
        this.tournamentStatus = tournamentStatus;
    }

    public String getTournamentStyle() {
        return tournamentStyle;
    }

    public void setTournamentStyle(String tournamentStyle) {
        this.tournamentStyle = tournamentStyle;
    }

    public List<Long> getRegisteredPlayersId() {
        return registeredPlayersId;
    }

    public void setRegisteredPlayersId(List<Long> registeredPlayersId) {
        this.registeredPlayersId = registeredPlayersId;
    }

    public Map<Long, Integer> getRankings() {
        return rankings;
    }

    public void setRankings(Map<Long, Integer> rankings) {
        this.rankings = rankings;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMinElo() {
        return minElo;
    }

    public void setMinElo(int minElo) {
        this.minElo = minElo;
    }

    public int getMaxElo() {
        return maxElo;
    }

    public void setMaxElo(int maxElo) {
        this.maxElo = maxElo;
    }

    public LocalDateTime getRegistrationCutOff() {
        return registrationCutOff;
    }

    public void setRegistrationCutOff(LocalDateTime registrationCutOff) {
        this.registrationCutOff = registrationCutOff;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int round) {
        this.roundNumber = round;
    }

    private int minPlayers;
    private int minElo;
    private int maxElo;
    private LocalDateTime registrationCutOff;
    private int roundNumber;

    // Default Constructor
    public TournamentDTO() {}


    
}
