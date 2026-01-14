package com.kfactor.matchmaking.service;

import com.kfactor.matchmaking.model.Match;
import com.kfactor.matchmaking.model.Player;
import com.kfactor.matchmaking.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerService playerService;

    public MatchService(MatchRepository matchRepository, PlayerService playerService) {
        this.matchRepository = matchRepository;
        this.playerService = playerService;
    }

    @Transactional
    public Match recordMatch(Long playerAId, Long playerBId, Long winnerId) {
        Player playerA = playerService.getPlayerById(playerAId);
        Player playerB = playerService.getPlayerById(playerBId);
        Player winner = playerService.getPlayerById(winnerId);

        // Validate that winner is one of the players
        if (!winner.getId().equals(playerAId) && !winner.getId().equals(playerBId)) {
            throw new IllegalArgumentException("Winner must be one of the players in the match");
        }

        // Create and save match
        Match match = new Match(playerA, playerB, winner);
        match = matchRepository.save(match);

        // Update ratings using K-factor algorithm
        updateRatings(playerA, playerB, winner);

        return match;
    }

    private void updateRatings(Player playerA, Player playerB, Player winner) {
        int K = 32; // K-factor for rating changes

        double expectedA = 1.0 / (1.0 + Math.pow(10, (playerB.getRating() - playerA.getRating()) / 400.0));
        double expectedB = 1.0 / (1.0 + Math.pow(10, (playerA.getRating() - playerB.getRating()) / 400.0));

        double scoreA = winner.getId().equals(playerA.getId()) ? 1.0 : 0.0;
        double scoreB = winner.getId().equals(playerB.getId()) ? 1.0 : 0.0;

        int newRatingA = (int) Math.round(playerA.getRating() + K * (scoreA - expectedA));
        int newRatingB = (int) Math.round(playerB.getRating() + K * (scoreB - expectedB));

        playerService.updateRating(playerA.getId(), newRatingA);
        playerService.updateRating(playerB.getId(), newRatingB);
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public List<Match> getMatchesForPlayer(Long playerId) {
        Player player = playerService.getPlayerById(playerId);
        return matchRepository.findByPlayerAOrPlayerBOrderByPlayedAtDesc(player, player);
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + id));
    }
}