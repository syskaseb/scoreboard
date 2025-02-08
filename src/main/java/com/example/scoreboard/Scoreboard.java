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
     * Updates the score for an ongoing match.
     * <p>
     * Since {@code Match} is immutable, this method locates the match based on the teams provided
     * in the {@code match} parameter and creates a new updated instance with the provided scores.
     * </p>
     *
     * @param match     the match instance identifying the match to update; the teams must match an existing match.
     * @param homeScore the new score for the home team; must be non-negative.
     * @param awayScore the new score for the away team; must be non-negative.
     * @throws IllegalArgumentException if the match is not found or if any score is negative.
     */
    public void updateScore(Match match, int homeScore, int awayScore) {
        validator.validateScore(homeScore, awayScore);
        repository.updateMatchScore(match.homeTeam(), match.awayTeam(), homeScore, awayScore);
    }

    /**
     * Returns an immutable summary of the current matches.
     *
     * @return an unmodifiable list of matches
     */
    public List<Match> getSummary() {
        return repository.getAllMatches();
    }

    /**
     * Finishes a match by removing it from storage.
     *
     * @param match the match instance to finish.
     */
    public void finishMatch(Match match) {
        repository.removeMatch(match);
    }
}
