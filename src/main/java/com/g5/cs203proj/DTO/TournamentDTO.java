package com.g5.cs203proj.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public class TournamentDTO {

    private Long tournamentId;

    @NotBlank(message = "Tournament name cannot be blank")
    @Size(min = 3, max = 100, message = "Tournament name must be between 3 and 100 characters")
    private String name;

    private List<Long> tournamentMatchHistoryId = new ArrayList<>();  // Only include match IDs, not the full Match objects

    @NotBlank(message = "Tournament status cannot be blank")
    private String tournamentStatus;

    @NotBlank(message = "Tournament style cannot be blank")
    private String tournamentStyle = "random"; // random by default 

    private List<Long> registeredPlayersId = new ArrayList<>();

    private Map<Long, Integer> rankings;

    @Positive(message = "Max players must be positive")
    private int maxPlayers;

    @Positive(message = "Min players must be positive")
    private int minPlayers;

    private int minElo;

    @Positive(message = "Max Elo rating must be positive")
    private int maxElo;

    private LocalDateTime registrationCutOff;

    // Default Constructor
    public TournamentDTO() {}


    
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
}
