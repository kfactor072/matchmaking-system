package com.kfactor.matchmaking.dto;

public class PlayerStatsDTO {
    private Long playerId;
    private String username;
    private int rating;
    private int totalMatches;
    private int wins;
    private int losses;
    private double winRate;

    public PlayerStatsDTO(Long playerId, String username, int rating, int totalMatches, int wins, int losses) {
        this.playerId = playerId;
        this.username = username;
        this.rating = rating;
        this.totalMatches = totalMatches;
        this.wins = wins;
        this.losses = losses;
        this.winRate = totalMatches > 0 ? (double) wins / totalMatches * 100 : 0.0;
    }

    // Getters
    public Long getPlayerId() { return playerId; }
    public String getUsername() { return username; }
    public int getRating() { return rating; }
    public int getTotalMatches() { return totalMatches; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public double getWinRate() { return winRate; }
}