package com.kfactor.matchmaking.service;

import com.kfactor.matchmaking.dto.PlayerStatsDTO;
import com.kfactor.matchmaking.model.Player;
import com.kfactor.matchmaking.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private PlayerService playerService;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private StatsService statsService;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = new Player("testuser");
        testPlayer.setRating(1200);
    }

    @Test
    void getPlayerStats_Success() {
        // Arrange
        when(playerService.getPlayerById(1L)).thenReturn(testPlayer);
        when(matchRepository.countMatchesByPlayer(testPlayer)).thenReturn(10);
        when(matchRepository.countWinsByPlayer(testPlayer)).thenReturn(7);

        // Act
        PlayerStatsDTO result = statsService.getPlayerStats(1L);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1200, result.getRating());
        assertEquals(10, result.getTotalMatches());
        assertEquals(7, result.getWins());
        assertEquals(3, result.getLosses());
        assertEquals(70.0, result.getWinRate(), 0.01);
    }

    @Test
    void getPlayerStats_WithNoMatches() {
        // Arrange
        when(playerService.getPlayerById(1L)).thenReturn(testPlayer);
        when(matchRepository.countMatchesByPlayer(testPlayer)).thenReturn(0);
        when(matchRepository.countWinsByPlayer(testPlayer)).thenReturn(0);

        // Act
        PlayerStatsDTO result = statsService.getPlayerStats(1L);

        // Assert
        assertEquals(0, result.getTotalMatches());
        assertEquals(0, result.getWins());
        assertEquals(0, result.getLosses());
        assertEquals(0.0, result.getWinRate());
    }

    @Test
    void getPlayerStats_CalculatesWinRateCorrectly() {
        // Arrange
        when(playerService.getPlayerById(1L)).thenReturn(testPlayer);
        when(matchRepository.countMatchesByPlayer(testPlayer)).thenReturn(20);
        when(matchRepository.countWinsByPlayer(testPlayer)).thenReturn(15);

        // Act
        PlayerStatsDTO result = statsService.getPlayerStats(1L);

        // Assert
        assertEquals(75.0, result.getWinRate(), 0.01);
    }
}