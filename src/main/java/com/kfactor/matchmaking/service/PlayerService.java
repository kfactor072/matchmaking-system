package com.kfactor.matchmaking.service;

import com.kfactor.matchmaking.model.Player;
import com.kfactor.matchmaking.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player createPlayer(String username) {
        if (playerRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        Player player = new Player(username);
        return playerRepository.save(player);
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + id));
    }

    public Player getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with username: " + username));
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Transactional
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new IllegalArgumentException("Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
    }

    @Transactional
    public Player updateRating(Long id, int newRating) {
        Player player = getPlayerById(id);
        player.setRating(newRating);
        return playerRepository.save(player);
    }

    public List<Player> getLeaderboard(int limit) {
        return playerRepository.findAll().stream()
                .sorted((p1, p2) -> Integer.compare(p2.getRating(), p1.getRating()))
                .limit(limit)
                .toList();
    }
}