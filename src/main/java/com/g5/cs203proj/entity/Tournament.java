package com.g5.cs203proj.entity;

import java.util.*;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="tournament_name")
    private String name;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JoinColumn(name = "tournament_id")  // Foreign key in the Match table
    private List<Match> tournamentMatchHistory = new ArrayList<>();

    private String tournamentStatus;

    @Column(name="tournament_style")
    private String tournamentStyle; // Can be "RANDOM" or "ROUND_ROBIN"

    @ManyToMany
    @JoinTable(
        name = "player_tournament",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "player_id"))
    private Set<Player> registeredPlayers = new HashSet<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Ranking> rankings;

    private int maxPlayers;
    private int minPlayers;

    private int minElo;
    private int maxElo;

    private LocalDateTime registrationCutOff;

    private int roundNumber;

    // Constructors, getters, and setters
    public Tournament() {
        this.rankings = new ArrayList<>();
    }

    public Tournament(String name, String tournamentStatus, String tournamentStyle, int maxPlayers, int minPlayers, int minElo, int maxElo, LocalDateTime registrationCutOff, int round) {
        this.name = name;
        this.tournamentStatus = tournamentStatus;
        this.tournamentStyle = tournamentStyle;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.minElo = minElo;
        this.maxElo = maxElo;
        this.registrationCutOff = registrationCutOff;
        this.roundNumber = round;
        this.rankings = new ArrayList<>();
    }

    // Getters and setters for all fields
    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int round) {
        this.roundNumber = round;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Match> getTournamentMatchHistory() {
        return tournamentMatchHistory;
    }

    public void addTestMatch(Match m) {
        this.tournamentMatchHistory.add(m);
    }

    public void setTournamentMatchHistory(List<Match> tournamentMatchHistory) {
        this.tournamentMatchHistory = tournamentMatchHistory;
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

    public Set<Player> getRegisteredPlayers() {
        return registeredPlayers;
    }

    public void setRegisteredPlayers(Set<Player> registeredPlayers) {
        this.registeredPlayers = registeredPlayers;
    }

    public List<Ranking> getRankings() {
        return rankings;
    }

    public void setRankings(List<Ranking> rankings) {
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
