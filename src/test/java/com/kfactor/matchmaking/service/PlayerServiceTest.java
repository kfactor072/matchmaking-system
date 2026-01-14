package com.kfactor.matchmaking.service;

import com.kfactor.matchmaking.model.Player;
import com.kfactor.matchmaking.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = new Player("testuser");
        testPlayer.setRating(1000);
    }

    @Test
    void createPlayer_Success() {
        // Arrange
        when(playerRepository.existsByUsername("testuser")).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenReturn(testPlayer);

        // Act
        Player result = playerService.createPlayer("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1000, result.getRating());
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void createPlayer_ThrowsException_WhenUsernameExists() {
        // Arrange
        when(playerRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.createPlayer("testuser");
        });
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void getPlayerById_Success() {
        // Arrange
        when(playerRepository.findById(1L)).thenReturn(Optional.of(testPlayer));

        // Act
        Player result = playerService.getPlayerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(playerRepository, times(1)).findById(1L);
    }

    @Test
    void getPlayerById_ThrowsException_WhenNotFound() {
        // Arrange
        when(playerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.getPlayerById(999L);
        });
    }

    @Test
    void getPlayerByUsername_Success() {
        // Arrange
        when(playerRepository.findByUsername("testuser")).thenReturn(Optional.of(testPlayer));

        // Act
        Player result = playerService.getPlayerByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getAllPlayers_ReturnsAllPlayers() {
        // Arrange
        Player player1 = new Player("alice");
        Player player2 = new Player("bob");
        when(playerRepository.findAll()).thenReturn(Arrays.asList(player1, player2));

        // Act
        List<Player> result = playerService.getAllPlayers();

        // Assert
        assertEquals(2, result.size());
        verify(playerRepository, times(1)).findAll();
    }

    @Test
    void updateRating_Success() {
        // Arrange
        when(playerRepository.findById(1L)).thenReturn(Optional.of(testPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(testPlayer);

        // Act
        Player result = playerService.updateRating(1L, 1200);

        // Assert
        assertEquals(1200, result.getRating());
        verify(playerRepository, times(1)).save(testPlayer);
    }

    @Test
    void deletePlayer_Success() {
        // Arrange
        when(playerRepository.existsById(1L)).thenReturn(true);

        // Act
        playerService.deletePlayer(1L);

        // Assert
        verify(playerRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePlayer_ThrowsException_WhenNotFound() {
        // Arrange
        when(playerRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.deletePlayer(999L);
        });
        verify(playerRepository, never()).deleteById(any());
    }

    @Test
    void getLeaderboard_ReturnsTopPlayers() {
        // Arrange
        Player player1 = new Player("alice");
        player1.setRating(1500);
        Player player2 = new Player("bob");
        player2.setRating(1300);
        Player player3 = new Player("charlie");
        player3.setRating(1100);

        when(playerRepository.findAll()).thenReturn(Arrays.asList(player1, player2, player3));

        // Act
        List<Player> result = playerService.getLeaderboard(2);

        // Assert
        assertEquals(2, result.size());
        assertEquals("alice", result.get(0).getUsername());
        assertEquals("bob", result.get(1).getUsername());
        assertTrue(result.get(0).getRating() >= result.get(1).getRating());
    }
}