package com.g5.cs203proj;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.g5.cs203proj.DTO.*;
import com.g5.cs203proj.entity.*;
import com.g5.cs203proj.repository.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.properties")
public class SpringBootIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private Tournament tournament;
    private Player adminPlayer;
    private Player regularPlayer;
    private Match match;

    private TestRestTemplate restTemplate;
    private TestRestTemplate adminTemplate;
    private TestRestTemplate userTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // Create admin user
        adminPlayer = new Player("admin_user", encoder.encode("admin123456"), "admin@test.com", "ROLE_ADMIN");
        adminPlayer.setGlobalEloRating(1500);
        adminPlayer.setEnabled(true);
        adminPlayer = playerRepository.save(adminPlayer);

        // Create regular user
        regularPlayer = new Player("regular_user", encoder.encode("user12345678"), "user@test.com", "ROLE_USER");
        regularPlayer.setGlobalEloRating(1500);
        regularPlayer.setEnabled(true);
        regularPlayer = playerRepository.save(regularPlayer);

        // Create test tournament
        tournament = new Tournament();
        tournament.setName("Test Tournament");
        tournament.setTournamentStatus("REGISTRATION");
        tournament.setTournamentStyle("ROUND ROBIN");
        tournament.setMinPlayers(2);
        tournament.setMaxPlayers(8);
        tournament.setMinElo(1000);
        tournament.setMaxElo(2000);
        tournament.setRegistrationCutOff(LocalDateTime.now().plusDays(7));
        tournament.setRegisteredPlayers(new HashSet<>());
        tournament.setTournamentMatchHistory(new ArrayList<>());
        tournament.setRankings(new ArrayList<>());
        tournament = tournamentRepository.save(tournament);

        // Create test match
        match = new Match();
        match.setTournament(tournament);
        match.setMatchStatus("NOT_STARTED");
        match = matchRepository.save(match);

        // Configure message converter with all supported media types
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.TEXT_PLAIN,
            MediaType.ALL
        ));

        // Set up rest templates with proper headers
        RestTemplateBuilder builder = new RestTemplateBuilder()
            .rootUri(baseUrl)
            .messageConverters(converter)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        restTemplate = new TestRestTemplate(builder);
        adminTemplate = new TestRestTemplate(builder, "admin_user", "admin123456");
        userTemplate = new TestRestTemplate(builder, "regular_user", "user12345678");
    }

    @AfterEach
    @Transactional
    void tearDown() {
        try {
            // Delete matches first
            matchRepository.deleteAll();
            matchRepository.flush();

            // Then delete tournament
            if (tournament != null) {
                tournamentRepository.deleteById(tournament.getId());
                tournamentRepository.flush();
            }

            // Finally delete players
            playerRepository.deleteAll();
            playerRepository.flush();
        } catch (Exception e) {
            System.err.println("Error in tearDown: " + e.getMessage());
        }
    }

    @Test
    void getAllTournaments_Success() {
        ResponseEntity<TournamentDTO[]> result = restTemplate.getForEntity("/tournaments", TournamentDTO[].class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().length);
    }

    @Test
    void getTournamentById_Success() {
        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity("/tournaments/{id}", TournamentDTO.class, tournament.getId());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(tournament.getName(), result.getBody().getName());
    }

    @Test
    void getTournamentById_NotFound() {
        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity("/tournaments/{id}", TournamentDTO.class, 999L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void registerPlayerToTournament_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        String url = String.format("/tournaments/%d/players?playerId=%d", tournament.getId(), regularPlayer.getId());

        ResponseEntity<TournamentDTO> result = userTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            TournamentDTO.class
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getRegisteredPlayersId().contains(regularPlayer.getId()));
    }

}
