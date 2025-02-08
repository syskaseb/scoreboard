package com.example.scoreboard;

import java.util.List;

/**
 * Manages live matches on the scoreboard.
 */
public class Scoreboard {

    private final MatchRepository repository = new MatchRepository();

    /**
     * Starts a match with an initial score of 0-0.
     *
     * @param homeTeam name of the home team; must not be null or blank
     * @param awayTeam name of the away team; must not be null or blank
     * @throws IllegalArgumentException if either team name is null or blank
     */
    public void startMatch(String homeTeam, String awayTeam) {
        if (homeTeam == null || homeTeam.isBlank()) {
            throw new IllegalArgumentException("Home team name cannot be empty.");
        }
        if (awayTeam == null || awayTeam.isBlank()) {
            throw new IllegalArgumentException("Away team name cannot be empty.");
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
