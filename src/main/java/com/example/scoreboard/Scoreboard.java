package com.example.scoreboard;

import java.util.List;

/**
 * Public API for managing live matches on the scoreboard.
 */
public class Scoreboard {

    private final MatchRepository repository = new MatchRepository();
    private final ScoreboardValidator validator = new ScoreboardValidator();

    /**
     * Starts a match with an initial score of 0-0.
     *
     * @param homeTeam name of the home team; must not be null, blank, or equal to awayTeam,
     *                 and must not already be in a match.
     * @param awayTeam name of the away team; must not be null, blank, or equal to homeTeam,
     *                 and must not already be in a match.
     * @throws IllegalArgumentException if validation fails.
     */
    public void startMatch(String homeTeam, String awayTeam) {
        validator.validateNewMatch(homeTeam, awayTeam, repository.getAllMatches());
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
