package com.kfactor.matchmaking.dto;

import jakarta.validation.constraints.NotNull;

public class MatchDTO {

    @NotNull(message = "Player A ID is required")
    private Long playerAId;

    @NotNull(message = "Player B ID is required")
    private Long playerBId;

    @NotNull(message = "Winner ID is required")
    private Long winnerId;

    // Constructors
    public MatchDTO() {}

    public MatchDTO(Long playerAId, Long playerBId, Long winnerId) {
        this.playerAId = playerAId;
        this.playerBId = playerBId;
        this.winnerId = winnerId;
    }

    // Getters and setters
    public Long getPlayerAId() { return playerAId; }
    public void setPlayerAId(Long playerAId) { this.playerAId = playerAId; }

    public Long getPlayerBId() { return playerBId; }
    public void setPlayerBId(Long playerBId) { this.playerBId = playerBId; }

    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }
}