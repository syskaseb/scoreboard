package com.example.scoreboard;

/**
 * A read-only view of a match state.
 */
public interface MatchSnapshot {
    String homeTeam();
    String awayTeam();
    int homeScore();
    int awayScore();
}