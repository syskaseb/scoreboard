package com.example.scoreboard;

import java.util.Comparator;
import java.util.List;

/**
 * Public thread-safe API for managing live matches on the scoreboard.
 */
public final class Scoreboard {

    private final MatchRepository repository = new MatchRepository();
    private final ScoreboardValidator validator = new ScoreboardValidator();

    // Package-private constructor: production code in other packages must use getInstance()
    Scoreboard() { }

    // Holder for lazy, thread-safe singleton initialization.
    private static class Holder {
        private static final Scoreboard INSTANCE = new Scoreboard();
    }

    /**
     * Returns the singleton instance of the Scoreboard.
     *
     * @return the singleton Scoreboard instance.
     */
    public static Scoreboard getInstance() {
        return Holder.INSTANCE;
    }

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
        // Synchronize to ensure that validate and add are atomic.
        synchronized (repository) {
            validator.validateNewMatch(homeTeam, awayTeam, repository.getAllMatches());
            Match match = new Match(homeTeam, awayTeam, 0, 0);
            repository.addMatch(match);
        }
    }

    /**
     * Updates the score for an ongoing match.
     * <p>
     * Since {@code Match} is immutable, this method locates the match based on the teams provided
     * in the {@code match} parameter and creates a new instance with the updated scores.
     * </p>
     *
     * @param match     the match instance identifying the match to update; the teams must match an existing match.
     * @param homeScore the new score for the home team; must be non-negative.
     * @param awayScore the new score for the away team; must be non-negative.
     * @throws IllegalArgumentException if the match is not found or if any score is negative.
     */
    public void updateScore(Match match, int homeScore, int awayScore) {
        synchronized (repository) {
            validator.validateScore(homeScore, awayScore);
            repository.updateMatchScore(match, homeScore, awayScore);
        }
    }

    /**
     * Returns an immutable summary of the current matches, ordered by the total score in descending order.
     * Matches with the same total score are ordered by recency (the match that was started later appears first).
     *
     * @return an unmodifiable list of matches, ordered by total score and recency.
     */
    public List<Match> getSummary() {
        synchronized (repository) {
            List<Match> matches = repository.getAllMatches();
            return matches.stream()
                    .sorted(Comparator
                            .comparingInt((Match m) -> m.homeScore() + m.awayScore()).reversed()
                            .thenComparing(matches::indexOf, Comparator.reverseOrder()))
                    .toList();
        }
    }

    /**
     * Finishes a match by removing it from storage.
     *
     * @param match the match instance to finish.
     */
    public void finishMatch(Match match) {
        synchronized (repository) {
            repository.removeMatch(match);
        }
    }
}