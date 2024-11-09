package com.g5.cs203proj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.g5.cs203proj.DTO.TournamentDTO;
import com.g5.cs203proj.entity.Tournament;
import com.g5.cs203proj.repository.TournamentRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringBootIntegrationTest {

    @LocalServerPort
    private int port;
    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @AfterEach
    void tearDown() {
        tournamentRepository.deleteAll();
    }


    @Test
    public void getAllTournaments_Success() throws Exception {
        // Arrange
        Tournament tournament1 = new Tournament();
        tournament1.setName("Chess Tournament 1");
        tournament1.setMinPlayers(2);
        tournament1.setMaxPlayers(10);
        tournament1.setTournamentStyle("Round Robin");
        
        Tournament tournament2 = new Tournament();
        tournament2.setName("Chess Tournament 2");
        tournament2.setMinPlayers(4);
        tournament2.setMaxPlayers(8);
        tournament2.setTournamentStyle("Swiss");

        tournamentRepository.saveAll(List.of(tournament1, tournament2));

        URI uri = new URI(baseUrl + port + "/tournaments");

        // Act
        ResponseEntity<TournamentDTO[]> result = restTemplate.getForEntity(uri, TournamentDTO[].class);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().length);
        assertEquals("Chess Tournament 1", result.getBody()[0].getName());
        assertEquals("Chess Tournament 2", result.getBody()[1].getName());
    }

    @Test
    public void getTournamentById_Success() throws Exception {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setName("Chess Tournament");
        tournament.setMinPlayers(2);
        tournament.setMaxPlayers(10);
        tournament.setTournamentStyle("Round Robin");

        Long tournamentId = tournamentRepository.save(tournament).getId();
        URI uri = new URI( baseUrl + port + "/tournaments/" + tournamentId);

        // Act: Perform the GET request
        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity(uri, TournamentDTO.class);

        // Assert: Check if the response is correct
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Chess Tournament", result.getBody().getName());
        assertEquals(2, result.getBody().getMinPlayers());
        assertEquals(10, result.getBody().getMaxPlayers());
    }


    @Test
    public void getTournamentById_Failure() throws Exception {
        // Arrange
        URI uri = new URI( baseUrl + port + "/tournaments/1" );
        // Act
        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity(uri, TournamentDTO.class); 
        // Assert: Check if the response is 404 Not Found
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }    

// @Test
// public void createTournament_Success() throws URISyntaxException {
//     // Set up TestRestTemplate with basic authentication
//     TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth("adminUsername", "adminPassword");

//     URI uri = new URI("http://localhost:" + port + "/tournaments");

//     // Create a TournamentDTO with required properties
//     TournamentDTO tournamentDTO = new TournamentDTO();
//     tournamentDTO.setName("New Tournament");
//     tournamentDTO.setMinPlayers(2);
//     tournamentDTO.setMaxPlayers(16);
//     tournamentDTO.setTournamentStyle("Knockout");

//     // Act: Send POST request to create tournament
//     ResponseEntity<Tournament> response = adminRestTemplate.postForEntity(uri, tournamentDTO, Tournament.class);

//     // Assert: Verify response status and data
//     assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
//     assertNotNull(response.getBody());
//     assertEquals("New Tournament", response.getBody().getName());
// }

}
