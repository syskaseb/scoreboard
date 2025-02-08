package com.example.scoreboard;

import java.util.List;

class ScoreboardValidator {

    void validateNewMatch(String homeTeam, String awayTeam, List<Match> existingMatches) {
        if (homeTeam == null || homeTeam.isBlank()) {
            throw new IllegalArgumentException("Home team name cannot be empty.");
        }
        if (awayTeam == null || awayTeam.isBlank()) {
            throw new IllegalArgumentException("Away team name cannot be empty.");
        }
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("Home and away teams must be different.");
        }
        for (Match m : existingMatches) {
            if (m.homeTeam().equals(homeTeam) || m.awayTeam().equals(homeTeam)
                    || m.homeTeam().equals(awayTeam) || m.awayTeam().equals(awayTeam)) {
                throw new IllegalArgumentException("One of the teams is already in a match.");
            }
        }
    }

    void validateScore(int homeScore, int awayScore) {
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Scores must be non-negative.");
        }
    }
}
