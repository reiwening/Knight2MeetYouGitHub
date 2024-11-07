package com.g5.cs203proj.DTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class PlayerDTO {
    
    private Long id;

    @NotNull(message="Username should not be null")
    @Size(min = 5, max = 20, message = "Username should be between 5 and 20 characters")
    private String username;

    @NotNull(message = "Password should not be null")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;

    @PositiveOrZero(message = "Global Elo Rating cannot be negative")
    private Double globalEloRating;

    private Set<Long> tournamentRegisteredIds = new HashSet<>();

    private List<Long> matchHistoryIds = new ArrayList<>();

    @NotNull(message="Authorities should not be null")
    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN", message = "Authorities must be either ROLE_USER or ROLE_ADMIN")
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
