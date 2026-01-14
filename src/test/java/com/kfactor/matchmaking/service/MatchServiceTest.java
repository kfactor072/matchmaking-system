package com.kfactor.matchmaking.service;

import com.kfactor.matchmaking.model.Match;
import com.kfactor.matchmaking.model.Player;
import com.kfactor.matchmaking.repository.MatchRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private MatchService matchService;

    private Player playerA;
    private Player playerB;

    @BeforeEach
    void setUp() {
        playerA = new Player("alice");
        playerA.setRating(1000);
        // Use reflection to set the ID since it's normally set by JPA
        setPlayerId(playerA, 1L);

        playerB = new Player("bob");
        playerB.setRating(1000);
        setPlayerId(playerB, 2L);
    }

    // Helper method to set the private id field using reflection
    private void setPlayerId(Player player, Long id) {
        try {
            var idField = Player.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(player, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void recordMatch_Success() {
        // Arrange
        when(playerService.getPlayerById(1L)).thenReturn(playerA);
        when(playerService.getPlayerById(2L)).thenReturn(playerB);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(playerService.updateRating(anyLong(), anyInt())).thenAnswer(invocation -> {
            Player p = invocation.getArgument(0).equals(1L) ? playerA : playerB;
            p.setRating(invocation.getArgument(1));
            return p;
        });

        // Act
        Match result = matchService.recordMatch(1L, 2L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(playerA, result.getPlayerA());
        assertEquals(playerB, result.getPlayerB());
        assertEquals(playerA, result.getWinner());
        verify(matchRepository, times(1)).save(any(Match.class));
        verify(playerService, times(2)).updateRating(anyLong(), anyInt());
    }

    @Test
    void recordMatch_ThrowsException_WhenWinnerIsNotAPlayer() {
        // Arrange
        Player playerC = new Player("charlie");
        setPlayerId(playerC, 3L);  // Add this line

        when(playerService.getPlayerById(1L)).thenReturn(playerA);
        when(playerService.getPlayerById(2L)).thenReturn(playerB);
        when(playerService.getPlayerById(3L)).thenReturn(playerC);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            matchService.recordMatch(1L, 2L, 3L);
        });
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void recordMatch_UpdatesRatingsCorrectly_WhenPlayerAWins() {
        // Arrange
        when(playerService.getPlayerById(1L)).thenReturn(playerA);
        when(playerService.getPlayerById(2L)).thenReturn(playerB);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Capture the ratings that updateRating is called with
        when(playerService.updateRating(eq(1L), anyInt())).thenAnswer(invocation -> {
            int newRating = invocation.getArgument(1);
            playerA.setRating(newRating);
            return playerA;
        });
        when(playerService.updateRating(eq(2L), anyInt())).thenAnswer(invocation -> {
            int newRating = invocation.getArgument(1);
            playerB.setRating(newRating);
            return playerB;
        });

        // Act
        matchService.recordMatch(1L, 2L, 1L);

        // Assert - Winner (playerA) should gain rating, loser (playerB) should lose rating
        assertTrue(playerA.getRating() > 1000, "Winner should gain rating points");
        assertTrue(playerB.getRating() < 1000, "Loser should lose rating points");
    }

    @Test
    void getAllMatches_ReturnsAllMatches() {
        // Arrange
        Match match1 = new Match(playerA, playerB, playerA);
        Match match2 = new Match(playerB, playerA, playerB);
        when(matchRepository.findAll()).thenReturn(Arrays.asList(match1, match2));

        // Act
        List<Match> result = matchService.getAllMatches();

        // Assert
        assertEquals(2, result.size());
        verify(matchRepository, times(1)).findAll();
    }

    @Test
    void getMatchesForPlayer_ReturnsPlayerMatches() {
        // Arrange
        Match match1 = new Match(playerA, playerB, playerA);
        when(playerService.getPlayerById(1L)).thenReturn(playerA);
        when(matchRepository.findByPlayerAOrPlayerBOrderByPlayedAtDesc(playerA, playerA))
                .thenReturn(Arrays.asList(match1));

        // Act
        List<Match> result = matchService.getMatchesForPlayer(1L);

        // Assert
        assertEquals(1, result.size());
        verify(matchRepository, times(1)).findByPlayerAOrPlayerBOrderByPlayedAtDesc(playerA, playerA);
    }

    @Test
    void getMatchById_Success() {
        // Arrange
        Match match = new Match(playerA, playerB, playerA);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Act
        Match result = matchService.getMatchById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(playerA, result.getWinner());
    }

    @Test
    void getMatchById_ThrowsException_WhenNotFound() {
        // Arrange
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            matchService.getMatchById(999L);
        });
    }

    @Test
    void recordMatch_UpdatesRatingsCorrectly_WhenPlayerBWins() {
        // Arrange
        when(playerService.getPlayerById(1L)).thenReturn(playerA);
        when(playerService.getPlayerById(2L)).thenReturn(playerB);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(playerService.updateRating(eq(1L), anyInt())).thenAnswer(invocation -> {
            int newRating = invocation.getArgument(1);
            playerA.setRating(newRating);
            return playerA;
        });
        when(playerService.updateRating(eq(2L), anyInt())).thenAnswer(invocation -> {
            int newRating = invocation.getArgument(1);
            playerB.setRating(newRating);
            return playerB;
        });

        // Act
        matchService.recordMatch(1L, 2L, 2L); // Player B wins this time

        // Assert - Winner (playerB) should gain rating, loser (playerA) should lose rating
        assertTrue(playerB.getRating() > 1000, "Winner should gain rating points");
        assertTrue(playerA.getRating() < 1000, "Loser should lose rating points");
    }

    @Test
    void recordMatch_WithDifferentRatings_AdjustsPointsCorrectly() {
        // Arrange - Higher rated player vs lower rated player
        playerA.setRating(1500); // Much higher rated
        playerB.setRating(1000);

        when(playerService.getPlayerById(1L)).thenReturn(playerA);
        when(playerService.getPlayerById(2L)).thenReturn(playerB);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int initialRatingA = playerA.getRating();
        int initialRatingB = playerB.getRating();

        when(playerService.updateRating(eq(1L), anyInt())).thenAnswer(invocation -> {
            int newRating = invocation.getArgument(1);
            playerA.setRating(newRating);
            return playerA;
        });
        when(playerService.updateRating(eq(2L), anyInt())).thenAnswer(invocation -> {
            int newRating = invocation.getArgument(1);
            playerB.setRating(newRating);
            return playerB;
        });

        // Act - Higher rated player (A) wins (expected outcome)
        matchService.recordMatch(1L, 2L, 1L);

        // Assert - Winner gains few points (expected to win), loser loses few points
        assertTrue(playerA.getRating() > initialRatingA, "Winner should gain rating");
        assertTrue(playerB.getRating() < initialRatingB, "Loser should lose rating");

        // The higher rated player should gain fewer points when beating lower rated player
        int pointsGainedByA = playerA.getRating() - initialRatingA;
        assertTrue(pointsGainedByA < 20, "Higher rated player should gain fewer points for expected win");
    }

    @Test
    void recordMatch_Upset_LowerRatedPlayerWins() {
        // Arrange - Lower rated player beats higher rated player (upset)
        playerA.setRating(1000);
        playerB.setRating(1500); // Much higher rated

        when(playerService.getPlayerById(1L)).thenReturn(playerA);
        when(playerService.getPlayerById(2L)).thenReturn(playerB);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int initialRatingA = playerA.getRating();

        when(playerService.updateRating(eq(1L), anyInt())).thenAnswer(invocation -> {
            int newRating = invocation.getArgument(1);
            playerA.setRating(newRating);
            return playerA;
        });
        when(playerService.updateRating(eq(2L), anyInt())).thenAnswer(invocation -> {
            int newRating = invocation.getArgument(1);
            playerB.setRating(newRating);
            return playerB;
        });

        // Act - Lower rated player (A) wins (upset!)
        matchService.recordMatch(1L, 2L, 1L);

        // Assert - Lower rated player should gain many points for upset win
        int pointsGainedByA = playerA.getRating() - initialRatingA;
        assertTrue(pointsGainedByA > 20, "Lower rated player should gain many points for upset win");
    }
}
