package com.kfactor.matchmaking.service;

import com.kfactor.matchmaking.dto.PlayerStatsDTO;
import com.kfactor.matchmaking.model.Player;
import com.kfactor.matchmaking.repository.MatchRepository;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private final PlayerService playerService;
    private final MatchRepository matchRepository;

    public StatsService(PlayerService playerService, MatchRepository matchRepository) {
        this.playerService = playerService;
        this.matchRepository = matchRepository;
    }

    public PlayerStatsDTO getPlayerStats(Long playerId) {
        Player player = playerService.getPlayerById(playerId);

        int totalMatches = matchRepository.countMatchesByPlayer(player);
        int wins = matchRepository.countWinsByPlayer(player);
        int losses = totalMatches - wins;

        return new PlayerStatsDTO(
                player.getId(),
                player.getUsername(),
                player.getRating(),
                totalMatches,
                wins,
                losses
        );
    }
}