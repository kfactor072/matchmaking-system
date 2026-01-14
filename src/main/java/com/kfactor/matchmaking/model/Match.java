package com.kfactor.matchmaking.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_a_id")
    private Player playerA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_b_id")
    private Player playerB;

    @ManyToOne(optional = false)
    @JoinColumn(name = "winner_id")
    private Player winner;

    @Column(nullable = false, updatable = false)
    private Instant playedAt;

    @PrePersist
    protected void onCreate() {
        playedAt = Instant.now();
    }

    // Constructors
    public Match() {}

    public Match(Player playerA, Player playerB, Player winner) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.winner = winner;
    }

    // Getters
    public Long getId() { return id; }
    public Player getPlayerA() { return playerA; }
    public Player getPlayerB() { return playerB; }
    public Player getWinner() { return winner; }
    public Instant getPlayedAt() { return playedAt; }

    // Setters
    public void setPlayerA(Player playerA) { this.playerA = playerA; }
    public void setPlayerB(Player playerB) { this.playerB = playerB; }
    public void setWinner(Player winner) { this.winner = winner; }
}