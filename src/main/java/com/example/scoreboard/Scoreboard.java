package com.example.scoreboard;

import java.util.List;

/**
 * Public API for managing live matches on the scoreboard.
 */
public class Scoreboard {

    private final MatchRepository repository = new MatchRepository();

    /**
     * Starts a match with an initial score of 0-0.
     *
     * @param homeTeam name of the home team; must not be null, blank, or equal to awayTeam
     * @param awayTeam name of the away team; must not be null, blank, or equal to homeTeam
     * @throws IllegalArgumentException if any team name is null, blank, if both teams are the same,
     *                                  or if either team is already in a match.
     */
    public void startMatch(String homeTeam, String awayTeam) {
        if (homeTeam == null || homeTeam.isBlank()) {
            throw new IllegalArgumentException("Home team name cannot be empty.");
        }
        if (awayTeam == null || awayTeam.isBlank()) {
            throw new IllegalArgumentException("Away team name cannot be empty.");
        }
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("Home and away teams must be different.");
        }
        // Check if either team is already in a match.
        for (Match m : repository.getAllMatches()) {
            if (m.homeTeam().equals(homeTeam) || m.awayTeam().equals(homeTeam)
                    || m.homeTeam().equals(awayTeam) || m.awayTeam().equals(awayTeam)) {
                throw new IllegalArgumentException("One of the teams is already in a match.");
            }
        }
        Match match = new Match(homeTeam, awayTeam, 0, 0);
        repository.addMatch(match);
    }

    /**
     * Returns an immutable summary of the current matches.
     *
     * @return an unmodifiable list of matches
     */
    public List<Match> getSummary() {
        return repository.getAllMatches();
    }
}
