package com.kfactor.matchmaking.controller;

import com.kfactor.matchmaking.dto.MatchDTO;
import com.kfactor.matchmaking.model.Match;
import com.kfactor.matchmaking.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<Match> recordMatch(@Valid @RequestBody MatchDTO matchDTO) {
        Match match = matchService.recordMatch(
                matchDTO.getPlayerAId(),
                matchDTO.getPlayerBId(),
                matchDTO.getWinnerId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Match>> getMatchesForPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(matchService.getMatchesForPlayer(playerId));
    }
}