package com.g5.cs203proj.entity;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;


// @Entity
// @Inheritance(strategy = InheritanceType.JOINED)  // You can choose other strategies like SINGLE_TABLE or TABLE_PER_CLASS
public class User {

    // private String username;
    // private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;
    // private String hashedPassword;
    

    // public User(String username, Long id, String hashedPassword) {
    //     this.username = username;
    //     this.id = id;
    //     this.hashedPassword = hashedPassword;
    // }

    // public User() {
        
    // }

    // public String getUsername() {
    //     return username;
    // }

    // public void setUsername(String username) {
    //     this.username = username;
    // }
}