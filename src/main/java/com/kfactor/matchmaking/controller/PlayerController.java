package com.kfactor.matchmaking.controller;

import com.kfactor.matchmaking.dto.PlayerDTO;
import com.kfactor.matchmaking.dto.PlayerStatsDTO;
import com.kfactor.matchmaking.model.Player;
import com.kfactor.matchmaking.service.PlayerService;
import com.kfactor.matchmaking.service.StatsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;
    private final StatsService statsService;

    public PlayerController(PlayerService playerService, StatsService statsService) {
        this.playerService = playerService;
        this.statsService = statsService;
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        Player player = playerService.createPlayer(playerDTO.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Player> getPlayerByUsername(@PathVariable String username) {
        return ResponseEntity.ok(playerService.getPlayerByUsername(username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Player>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerService.getLeaderboard(limit));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<PlayerStatsDTO> getPlayerStats(@PathVariable Long id) {
        return ResponseEntity.ok(statsService.getPlayerStats(id));
    }
}