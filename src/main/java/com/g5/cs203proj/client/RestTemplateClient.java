package com.g5.cs203proj.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.g5.cs203proj.entity.Player;

@Component
public class RestTemplateClient {

    private final RestTemplate template;

    public RestTemplateClient(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder
                .basicAuthentication("Player1", "Password1")
                .build();
    }


    // // This method should take a String URI and create a player
    // public Player addPlayerAutomatically(final String URI) {
    //     Player player = new Player("Player1", "Password1", "ROLE_USER");
    //     final Player returned = template.postForObject(URI, player, Player.class);
    //     return returned;
    // }
    

    
}
