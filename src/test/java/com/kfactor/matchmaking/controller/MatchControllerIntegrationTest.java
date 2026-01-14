package com.kfactor.matchmaking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfactor.matchmaking.dto.MatchDTO;
import com.kfactor.matchmaking.model.Match;
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
class MatchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    private Player playerA;
    private Player playerB;

    @BeforeEach
    void setUp() {
        matchRepository.deleteAll();
        playerRepository.deleteAll();

        playerA = playerRepository.save(new Player("alice"));
        playerB = playerRepository.save(new Player("bob"));
    }

    @Test
    void recordMatch_Success() throws Exception {
        MatchDTO matchDTO = new MatchDTO(playerA.getId(), playerB.getId(), playerA.getId());

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.playerA.username").value("alice"))
                .andExpect(jsonPath("$.playerB.username").value("bob"))
                .andExpect(jsonPath("$.winner.username").value("alice"));
    }

    @Test
    void recordMatch_ValidationError_NullPlayerA() throws Exception {
        MatchDTO matchDTO = new MatchDTO(null, playerB.getId(), playerB.getId());

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.playerAId").exists());
    }

    @Test
    void recordMatch_ValidationError_NullPlayerB() throws Exception {
        MatchDTO matchDTO = new MatchDTO(playerA.getId(), null, playerA.getId());

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.playerBId").exists());
    }

    @Test
    void recordMatch_ValidationError_NullWinner() throws Exception {
        MatchDTO matchDTO = new MatchDTO(playerA.getId(), playerB.getId(), null);

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.winnerId").exists());
    }

    @Test
    void recordMatch_Error_PlayerNotFound() throws Exception {
        MatchDTO matchDTO = new MatchDTO(999L, playerB.getId(), 999L);

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player not found with id: 999"));
    }

    @Test
    void recordMatch_Error_WinnerNotAPlayer() throws Exception {
        Player playerC = playerRepository.save(new Player("charlie"));
        MatchDTO matchDTO = new MatchDTO(playerA.getId(), playerB.getId(), playerC.getId());

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Winner must be one of the players in the match"));
    }

    @Test
    void getAllMatches_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllMatches_ReturnsMatchList() throws Exception {
        matchRepository.save(new Match(playerA, playerB, playerA));
        matchRepository.save(new Match(playerB, playerA, playerB));

        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMatchById_Success() throws Exception {
        Match match = matchRepository.save(new Match(playerA, playerB, playerA));

        mockMvc.perform(get("/api/matches/{id}", match.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winner.username").value("alice"));
    }

    @Test
    void getMatchById_NotFound() throws Exception {
        mockMvc.perform(get("/api/matches/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Match not found with id: 999"));
    }

    @Test
    void getMatchesForPlayer_Success() throws Exception {
        matchRepository.save(new Match(playerA, playerB, playerA));
        matchRepository.save(new Match(playerB, playerA, playerB));

        mockMvc.perform(get("/api/matches/player/{playerId}", playerA.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMatchesForPlayer_EmptyList() throws Exception {
        mockMvc.perform(get("/api/matches/player/{playerId}", playerA.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getMatchesForPlayer_NotFound() throws Exception {
        mockMvc.perform(get("/api/matches/player/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player not found with id: 999"));
    }
}