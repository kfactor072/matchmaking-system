package com.kfactor.matchmaking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfactor.matchmaking.dto.PlayerDTO;
import com.kfactor.matchmaking.model.Player;
import com.kfactor.matchmaking.repository.MatchRepository;
import com.kfactor.matchmaking.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @BeforeEach
    void setUp() {
        matchRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    void createPlayer_Success() throws Exception {
        PlayerDTO playerDTO = new PlayerDTO("testuser");

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.rating").value(1000))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createPlayer_ValidationError_UsernameTooShort() throws Exception {
        PlayerDTO playerDTO = new PlayerDTO("ab");

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    void createPlayer_ValidationError_UsernameBlank() throws Exception {
        PlayerDTO playerDTO = new PlayerDTO("");

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPlayer_Error_DuplicateUsername() throws Exception {
        // Create first player
        Player player = new Player("duplicate");
        playerRepository.save(player);

        // Try to create duplicate
        PlayerDTO playerDTO = new PlayerDTO("duplicate");

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void getAllPlayers_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllPlayers_ReturnsPlayerList() throws Exception {
        playerRepository.save(new Player("alice"));
        playerRepository.save(new Player("bob"));

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[1].username").value("bob"));
    }

    @Test
    void getPlayerById_Success() throws Exception {
        Player player = playerRepository.save(new Player("testuser"));

        mockMvc.perform(get("/api/players/{id}", player.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.rating").value(1000));
    }

    @Test
    void getPlayerById_NotFound() throws Exception {
        mockMvc.perform(get("/api/players/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player not found with id: 999"));
    }

    @Test
    void getPlayerByUsername_Success() throws Exception {
        playerRepository.save(new Player("alice"));

        mockMvc.perform(get("/api/players/username/{username}", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void getPlayerByUsername_NotFound() throws Exception {
        mockMvc.perform(get("/api/players/username/nonexistent"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player not found with username: nonexistent"));
    }

    @Test
    void deletePlayer_Success() throws Exception {
        Player player = playerRepository.save(new Player("testuser"));

        mockMvc.perform(delete("/api/players/{id}", player.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/players/{id}", player.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePlayer_NotFound() throws Exception {
        mockMvc.perform(delete("/api/players/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player not found with id: 999"));
    }

    @Test
    void getLeaderboard_Default() throws Exception {
        Player p1 = new Player("alice");
        p1.setRating(1500);
        Player p2 = new Player("bob");
        p2.setRating(1300);
        Player p3 = new Player("charlie");
        p3.setRating(1100);

        playerRepository.save(p1);
        playerRepository.save(p2);
        playerRepository.save(p3);

        mockMvc.perform(get("/api/players/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].rating").value(1500))
                .andExpect(jsonPath("$[1].username").value("bob"))
                .andExpect(jsonPath("$[2].username").value("charlie"));
    }

    @Test
    void getLeaderboard_WithLimit() throws Exception {
        playerRepository.save(new Player("p1"));
        playerRepository.save(new Player("p2"));
        playerRepository.save(new Player("p3"));

        mockMvc.perform(get("/api/players/leaderboard?limit=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getPlayerStats_Success() throws Exception {
        Player player = playerRepository.save(new Player("testuser"));

        mockMvc.perform(get("/api/players/{id}/stats", player.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.rating").value(1000))
                .andExpect(jsonPath("$.totalMatches").value(0))
                .andExpect(jsonPath("$.wins").value(0))
                .andExpect(jsonPath("$.losses").value(0))
                .andExpect(jsonPath("$.winRate").value(0.0));
    }

    @Test
    void getPlayerStats_NotFound() throws Exception {
        mockMvc.perform(get("/api/players/999/stats"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player not found with id: 999"));
    }
}