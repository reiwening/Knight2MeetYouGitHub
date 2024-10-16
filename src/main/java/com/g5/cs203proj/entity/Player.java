package com.g5.cs203proj.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.g5.cs203proj.entity.Match;

import java.util.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

// import lombok.*;

@Entity
// This class implements the UserDetails interface, which is required by Spring Security to manage user authentication
public class Player implements UserDetails   {

    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;

    @NotNull(message="Username should not be null")
    @Size(min = 5, max = 20, message = "Username should be between 5 and 20 characters")
    private String username;

    @NotNull(message = "Password should not be null")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;

    @NotNull(message="Authorities should not be null")
    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN", message = "Authorities must be either ROLE_USER or ROLE_ADMIN")
    // We define two roles/authorities: ROLE_USER or ROLE_ADMIN
    private String authorities;

    private double globalEloRating;

    @ManyToMany 
// @JsonIgnore
    @JoinTable(
        name = "player_tournament", 
        joinColumns = @JoinColumn(name = "player_id"), 
        inverseJoinColumns = @JoinColumn(name = "tournament_id"))
    private Set<Tournament> tournamentRegistered;
// can make it Set<Tournament>
    
    @OneToMany(mappedBy = "player1")
    @JsonIgnore
    private List<Match> matchesAsPlayer1;

    
    @OneToMany(mappedBy = "player2")
    @JsonIgnore
    private List<Match> matchesAsPlayer2;

    
    @Transient // This field is not persisted directly, but computed
// @JsonIgnore
    private List<Match> matchHistory;

    public void setTournamentRegistered(Set<Tournament> tournamentRegistered) {
        this.tournamentRegistered = tournamentRegistered;
    }


    public Player() {
    
    }
    

    public Player(String username, String password, String authorities) {
        // this.id = id;
        this.username = username;
        this.authorities =authorities;
        this.password = password;
    }

    public Player(String username, String password, String authorities, double globalEloRating) {
        // this.id = id;
        this.username = username;
        this.authorities =authorities;
        this.password = password;
    }

    public Long getId() {
        return this.id;
    }


    public double getGlobalEloRating() {
        return globalEloRating;
    }

    public void setGlobalEloRating(double globalEloRating) {
        this.globalEloRating = globalEloRating;
    }

    // makes me a ROLE_USER or ROLE_ADMIN object 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(authorities));
    }
    

    // Getter for matchHistory which consolidates both lists
    // Getter for matchHistory which consolidates both lists
    public List<Match> getMatchHistory() {
        if (matchHistory == null) {
            matchHistory = new ArrayList<>();  // Initialize the list if it's null
        }
        matchHistory.clear();  // Clear the list to ensure no duplicate entries
        if (matchesAsPlayer1 != null) {
            matchHistory.addAll(matchesAsPlayer1);
        }
        if (matchesAsPlayer2 != null) {
            matchHistory.addAll(matchesAsPlayer2);
        }
        return matchHistory;
    }

    
    @Override
    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
        
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    // need to implement all methods in UserDetails first 
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
    
    public Set<Tournament> getTournamentRegistered() {
        return tournamentRegistered;

    }

    public List<Match> getMatchesAsPlayer1() {
        return matchesAsPlayer1;
    }


    public Match addMatchesAsPlayer1(Match match) {
        this.matchesAsPlayer1.add(match);
        return match;
    }


    public List<Match> getMatchesAsPlayer2() {
        return matchesAsPlayer2;
    }

    public Match addMatchesAsPlayer2(Match match) {
        this.matchesAsPlayer2.add(match);
        return match;
    }
}

