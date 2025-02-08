package com.example.scoreboard;

/**
 * Immutable record representing a match.
 */
public record Match(String homeTeam, String awayTeam, int homeScore, int awayScore) {
}
