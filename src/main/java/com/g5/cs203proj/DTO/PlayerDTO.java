package com.g5.cs203proj.DTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerDTO {
    
    private Long id;
    private String username;
    private String password;
    private Double globalEloRating;
    private Set<Long> tournamentRegisteredIds = new HashSet<>();
    private List<Long> matchHistoryIds = new ArrayList<>();
    private String authorities;

    public PlayerDTO(Long id, String username, String password, Double globalEloRating, Set<Long> tournamentRegisteredIds,
            List<Long> matchHistoryIds, String authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.globalEloRating = globalEloRating;
        this.tournamentRegisteredIds = tournamentRegisteredIds;
        this.matchHistoryIds = matchHistoryIds;
        this.authorities = authorities;
    }


    public String getPassword() {
        return password;
    }


    // public void setPassword(String password) {
    //     this.password = password;
    // }


    public PlayerDTO(){}


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public Double getGlobalEloRating() {
        return globalEloRating;
    }


    public void setGlobalEloRating(Double globalEloRating) {
        this.globalEloRating = globalEloRating;
    }


    public Set<Long> getTournamentRegisteredIds() {
        return tournamentRegisteredIds;
    }


    public void setTournamentRegisteredIds(Set<Long> tournamentRegisteredIds) {
        this.tournamentRegisteredIds = tournamentRegisteredIds;
    }


    public List<Long> getMatchHistoryIds() {
        return matchHistoryIds;
    }


    public void setMatchHistoryIds(List<Long> matchHistoryIds) {
        this.matchHistoryIds = matchHistoryIds;
    }


    public String getAuthorities() {
        return authorities;
    }


    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }


}
