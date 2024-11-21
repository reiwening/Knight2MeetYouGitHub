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
        tournament = tournamentRepository.save(tournament);

        // Create test match
        match = new Match();
        match.setTournament(tournament);
        match.setMatchStatus("NOT_STARTED");
        match = matchRepository.save(match);

        // Configure message converter
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));

        // Set up rest templates
        RestTemplateBuilder builder = new RestTemplateBuilder()
            .rootUri(baseUrl)
            .messageConverters(converter)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        restTemplate = new TestRestTemplate(builder);
        adminTemplate = new TestRestTemplate(builder, "admin_user", "admin123456");
        userTemplate = new TestRestTemplate(builder, "regular_user", "user12345678");
    }

    @AfterEach
    void tearDown() {
        matchRepository.deleteAll();
        tournamentRepository.deleteAll();
        playerRepository.deleteAll();
    }

    // Public Endpoint Tests

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

    // Admin Endpoint Tests

    // @Test
    // void createTournament_Success() {
    //     TournamentDTO newTournament = new TournamentDTO();
    //     newTournament.setName("New Tournament");
    //     newTournament.setTournamentStatus("REGISTRATION");
    //     newTournament.setTournamentStyle("ROUND ROBIN");
    //     newTournament.setMinPlayers(2);
    //     newTournament.setMaxPlayers(8);

    //     HttpEntity<TournamentDTO> request = new HttpEntity<>(newTournament);
    //     ResponseEntity<TournamentDTO> result = adminTemplate.postForEntity("/tournaments", request, TournamentDTO.class);

    //     assertEquals(HttpStatus.CREATED, result.getStatusCode());
    //     assertNotNull(result.getBody());
    //     assertEquals("New Tournament", result.getBody().getName());
    // }

    // @Test
    // void createTournament_Forbidden() {
    //     TournamentDTO newTournament = new TournamentDTO();
    //     newTournament.setName("New Tournament");
    //     newTournament.setTournamentStatus("REGISTRATION");
    //     newTournament.setTournamentStyle("ROUND ROBIN");

    //     HttpEntity<TournamentDTO> request = new HttpEntity<>(newTournament);
    //     ResponseEntity<String> result = userTemplate.postForEntity("/tournaments", request, String.class);

    //     assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    // }

    // Authenticated User Tests

    @Test
    void registerPlayerToTournament_Success() {
        ResponseEntity<TournamentDTO> result = userTemplate.postForEntity(
            "/tournaments/{tournamentId}/players?playerId={playerId}", 
            null, 
            TournamentDTO.class,
            tournament.getId(),
            regularPlayer.getId()
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getRegisteredPlayersId().contains(regularPlayer.getId()));
    }

    // @Test
    // void registerPlayerToTournament_Forbidden() {
    //     ResponseEntity<String> result = restTemplate.postForEntity(
    //         "/tournaments/{tournamentId}/players?playerId={playerId}", 
    //         null, 
    //         String.class,
    //         tournament.getId(),
    //         regularPlayer.getId()
    //     );

    //     assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    // }

    // @Test
    // void startTournament_Success() {
    //     tournament.getRegisteredPlayers().add(regularPlayer);
    //     tournament.getRegisteredPlayers().add(adminPlayer);
    //     tournamentRepository.save(tournament);

    //     ResponseEntity<TournamentDTO> result = adminTemplate.exchange(
    //         "/tournaments/{id}/start-or-cancel",
    //         HttpMethod.PUT,
    //         null,
    //         TournamentDTO.class,
    //         tournament.getId()
    //     );

    //     assertEquals(HttpStatus.OK, result.getStatusCode());
    //     assertNotNull(result.getBody());
    //     assertEquals("IN PROGRESS", result.getBody().getTournamentStatus());
    // }

    @Test
    void getTournamentMatches_Success() {
        match.setTournament(tournament);
        matchRepository.save(match);
        tournament.getTournamentMatchHistory().add(match);
        tournamentRepository.save(tournament);

        ResponseEntity<List> result = restTemplate.getForEntity(
            "/tournaments/{id}/matches",
            List.class,
            tournament.getId()
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
    }

    // @Test
    // void updateTournamentStatus_AdminOnly() {
    //     ResponseEntity<TournamentDTO> adminResult = adminTemplate.exchange(
    //         "/tournaments/{id}/status?status={status}",
    //         HttpMethod.PUT,
    //         null,
    //         TournamentDTO.class,
    //         tournament.getId(),
    //         "IN_PROGRESS"
    //     );
    //     assertEquals(HttpStatus.OK, adminResult.getStatusCode());

    //     ResponseEntity<String> userResult = userTemplate.exchange(
    //         "/tournaments/{id}/status?status={status}",
    //         HttpMethod.PUT,
    //         null,
    //         String.class,
    //         tournament.getId(),
    //         "IN_PROGRESS"
    //     );
    //     assertEquals(HttpStatus.FORBIDDEN, userResult.getStatusCode());
    // }
}
