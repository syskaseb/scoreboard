package com.example.scoreboard;

import java.util.List;

class ScoreboardValidator {

    /**
     * Validates the input parameters for starting a new match.
     *
     * @param homeTeam       the home team name
     * @param awayTeam       the away team name
     * @param existingMatches the list of matches already in progress
     * @throws IllegalArgumentException if a validation rule is violated
     */
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
}
