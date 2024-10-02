package com.g5.cs203proj;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.g5.cs203proj.client.RestTemplateClient;
import com.g5.cs203proj.entity.Player;
import com.g5.cs203proj.repository.PlayerRepository;


@SpringBootApplication
public class Cs203projApplication {

	public static void main(String[] args) {
		SpringApplication.run(Cs203projApplication.class, args);
		// ApplicationContext ctx = SpringApplication.run(Cs203projApplication.class, args);

		// RestTemplateClient client = ctx.getBean(RestTemplateClient.class);
		// // Call to add the player automatically
        // Player newPlayer = client.addPlayerAutomatically("http://localhost:8080/players");

		// if (newPlayer != null) {
        //     System.out.println("ADD PLAYER: " + newPlayer.getUsername());
        // } else {
        //     System.out.println("Failed to add player.");
        // }
		

	}

}
