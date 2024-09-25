package com.g5.cs203proj.entity;

import java.util.*;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Admin extends User {


    @OneToMany(mappedBy = "admin")
    private List<Tournament> tournaments;  // One admin can manage multiple tournaments

    // Getters and Setters for tournaments

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

}
