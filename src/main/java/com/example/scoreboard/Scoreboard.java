package com.example.scoreboard;

import java.util.Comparator;
import java.util.List;

/**
 * Public thread-safe API for managing live matches on the scoreboard.
 */
public final class Scoreboard {

    private final MatchRepository repository = new MatchRepository();
    private final ScoreboardValidator validator = new ScoreboardValidator();

    Scoreboard() { }

    private static class Holder {
        private static final Scoreboard INSTANCE = new Scoreboard();
    }

    public static Scoreboard getInstance() {
        return Holder.INSTANCE;
    }

    public void startMatch(String homeTeam, String awayTeam) {
        synchronized (repository) {
            validator.validateNewMatch(homeTeam, awayTeam, repository.getAllMatches());
            repository.addMatch(homeTeam, awayTeam, 0, 0);
        }
    }

    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        synchronized (repository) {
            validator.validateScore(homeScore, awayScore);
            repository.updateMatchScore(homeTeam, awayTeam, homeScore, awayScore);
        }
    }

    public void finishMatch(String homeTeam, String awayTeam) {
        synchronized (repository) {
            repository.removeMatch(homeTeam, awayTeam);
        }
    }

    /**
     * Returns an immutable summary of the current matches, ordered by total score descending
     * and, for ties, by recency (the match that was started later appears first).
     */
    public List<Match> getSummary() {
        synchronized (repository) {
            // getAllMatches returns the LinkedHashSet as a list in insertion order.
            // We'll sort by total score descending, then by insertion recency descending.
            List<Match> matches = repository.getAllMatches();
            return matches.stream()
                    .sorted(Comparator
                            .comparingInt((Match m) -> m.homeScore() + m.awayScore()).reversed()
                            .thenComparing(matches::indexOf, Comparator.reverseOrder()))
                    .toList();
        }
    }
}
