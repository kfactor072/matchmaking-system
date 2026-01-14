package com.kfactor.matchmaking.repository;

import com.kfactor.matchmaking.model.Match;
import com.kfactor.matchmaking.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByPlayerAOrPlayerBOrderByPlayedAtDesc(Player playerA, Player playerB);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.playerA = :player OR m.playerB = :player")
    int countMatchesByPlayer(@Param("player") Player player);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.winner = :player")
    int countWinsByPlayer(@Param("player") Player player);
}