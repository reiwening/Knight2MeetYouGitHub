package com.g5.cs203proj.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.g5.cs203proj.entity.Match;

import java.util.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

@Entity
public class Player implements UserDetails {

    public void setId(Long id) {
        this.id = id;
    }

    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;

    @NotNull(message="Username should not be null")
    @Size(min = 5, max = 20, message = "Username should be between 5 and 20 characters")
    private String username;

    @NotNull(message = "Password should not be null")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;

    @NotNull(message="Email should not be null")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull(message="Authorities should not be null")
    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN", message = "Authorities must be either ROLE_USER or ROLE_ADMIN")
    private String authorities;

    private double globalEloRating;

    private boolean enabled;

    @ManyToMany 
    @JsonIgnore
    @JoinTable(
        name = "player_tournament", 
        joinColumns = @JoinColumn(name = "player_id"), 
        inverseJoinColumns = @JoinColumn(name = "tournament_id"))
    private Set<Tournament> tournamentRegistered = new HashSet<>();
    
    @OneToMany(mappedBy = "player1")
    @JsonIgnore
    private List<Match> matchesAsPlayer1 = new ArrayList<>();

    @OneToMany(mappedBy = "player2")
    @JsonIgnore
    private List<Match> matchesAsPlayer2 = new ArrayList<>();

    @Transient
    private List<Match> matchHistory = new ArrayList<>();

    public void setTournamentRegistered(Set<Tournament> tournamentRegistered) {
        this.tournamentRegistered = tournamentRegistered;
    }

    public Player() {}

    public Player(String username, String password, String email, String authorities) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }

    public Player(String username, String password, String email, String authorities, double globalEloRating, boolean enabled) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
        this.globalEloRating = globalEloRating;
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(authorities));
    }

    public void setMatchHistory(List<Match> matchHistory) {
        this.matchHistory = matchHistory;
    }

    public List<Match> getMatchHistory() {
        List<Match> combinedMatchHistory = new ArrayList<>();
        if (matchesAsPlayer1 != null) {
            combinedMatchHistory.addAll(matchesAsPlayer1);
        }
        if (matchesAsPlayer2 != null) {
            combinedMatchHistory.addAll(matchesAsPlayer2);
        }
        return combinedMatchHistory;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


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

    public boolean isAdmin() {
        return this.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
