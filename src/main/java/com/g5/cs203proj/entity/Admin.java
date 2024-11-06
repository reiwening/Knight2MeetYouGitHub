// package com.g5.cs203proj.entity;

// import java.util.*;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.OneToMany;

// @Entity
// public class Admin {


//     private String username;
//     private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;
//     private String hashedPassword;

//     @OneToMany(mappedBy = "admin")
//     private List<Tournament> tournaments;  // One admin can manage multiple tournaments

//     // Getters and Setters for tournaments

//     public List<Tournament> getTournaments() {
//         return tournaments;
//     }

//     public Admin(String username, String hashedPassword) {
//         this.username = username;
//         this.hashedPassword = hashedPassword;
//     }

//     public void setTournaments(List<Tournament> tournaments) {
//         this.tournaments = tournaments;
//     }

//     public String getUsername() {
//         return username;
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

// }
