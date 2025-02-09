package com.example.scoreboard;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class MatchRepository {

    private static final Logger logger = Logger.getLogger(MatchRepository.class.getName());
    // LinkedHashSet preserves insertion order and ensures uniqueness by equals/hashCode.
    private final Set<Match> matches = new LinkedHashSet<>();

    synchronized void addMatch(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        Match newMatch = new Match(homeTeam, awayTeam, homeScore, awayScore);
        // If the match is already in the set, add() returns false.
        if (!matches.add(newMatch)) {
            logger.log(Level.WARNING,
                    "Attempted to add duplicate match: {0} vs {1}",
                    new Object[]{homeTeam, awayTeam});
        }
    }

    synchronized List<Match> getAllMatches() {
        // Return an immutable snapshot
        return List.copyOf(matches);
    }

    synchronized void updateMatchScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        // Find the existing match by its teams (scores ignored).
        Match dummy = new Match(homeTeam, awayTeam, 0, 0);
        boolean removed = matches.remove(dummy);
        if (!removed) {
            logger.log(Level.WARNING,
                    "Attempted to update non-existent match: {0} vs {1}",
                    new Object[]{homeTeam, awayTeam});
            throw new IllegalArgumentException("Match not found.");
        }
        // Add the new match with updated scores.
        matches.add(new Match(homeTeam, awayTeam, homeScore, awayScore));
    }

    synchronized void removeMatch(String homeTeam, String awayTeam) {
        Match dummy = new Match(homeTeam, awayTeam, 0, 0);
        boolean removed = matches.remove(dummy);
        if (!removed) {
            logger.log(Level.WARNING,
                    "Attempted to remove a non-existent match: {0} vs {1}",
                    new Object[]{homeTeam, awayTeam});
        }
    }
}
