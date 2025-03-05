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
    Scoreboard() {
    }

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
        synchronized (repository) {
            validator.validateNewMatch(homeTeam, awayTeam);
            repository.addMatch(homeTeam, awayTeam, 0, 0);
        }
    }

    /**
     * Updates the score for an ongoing match (identified by homeTeam and awayTeam).
     *
     * @param homeTeam  the home team name; must match an existing match.
     * @param awayTeam  the away team name; must match an existing match.
     * @param homeScore the new score for the home team; must be non-negative.
     * @param awayScore the new score for the away team; must be non-negative.
     * @throws IllegalArgumentException if the match is not found or if any score is negative.
     */
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) throws IllegalArgumentException {
        synchronized (repository) {
            validator.validateScore(homeScore, awayScore);
            repository.updateMatchScore(homeTeam, awayTeam, homeScore, awayScore);
        }
    }

    /**
     * Finishes a match (removes it) from the scoreboard, identified by homeTeam and awayTeam.
     *
     * @param homeTeam the home team name; must match an existing match.
     * @param awayTeam the away team name; must match an existing match.
     */
    public void finishMatch(String homeTeam, String awayTeam) {
        synchronized (repository) {
            repository.removeMatch(homeTeam, awayTeam);
        }
    }

    /**
     * Returns an immutable summary of the current matches, ordered by the total score in descending order.
     * Matches with the same total score are ordered by recency (the match that was started later appears first).
     *
     * @return an unmodifiable list of matche snapshots, ordered by total score and recency.
     */
    public synchronized List<MatchSnapshot> getSummary() {
        List<Match> matches;
        synchronized (repository) {
            matches = repository.getAllMatches();
        }
        return matches.parallelStream()
                .sorted(Comparator
                        .comparingInt((Match m) -> m.homeScore() + m.awayScore()).reversed()
                        .thenComparing(Match::insertionOrder, Comparator.reverseOrder()))
                .map(MatchSnapshot.class::cast)
                .toList();
    }
}
