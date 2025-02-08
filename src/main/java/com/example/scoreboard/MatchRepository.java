package com.example.scoreboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class MatchRepository {
    private static final Logger logger = Logger.getLogger(MatchRepository.class.getName());
    // LinkedHashMap preserves insertion order.
    private final Map<String, Match> matches = new LinkedHashMap<>();

    synchronized void addMatch(Match match) {
        String key = generateKey(match);
        if (matches.putIfAbsent(key, match) != null) {
            logger.log(Level.WARNING, "Attempted to add duplicate match: {0} (key: {1})", new Object[]{match, key});
        }
    }

    synchronized List<Match> getAllMatches() {
        return List.copyOf(matches.values());
    }

    synchronized void updateMatchScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        String key = generateKey(new Match(homeTeam, awayTeam, 0, 0));
        Match existing = matches.get(key);
        if (existing == null) {
            logger.log(Level.WARNING, "Attempted to update non-existent match: {0} (key: {1})",
                    new Object[]{homeTeam + " vs " + awayTeam, key});
            throw new IllegalArgumentException("Match not found.");
        }
        Match updated = new Match(existing.homeTeam(), existing.awayTeam(), homeScore, awayScore);
        matches.put(key, updated);
    }

    synchronized void removeMatch(Match match) {
        String key = generateKey(match);
        Match removed = matches.remove(key);
        if (removed == null) {
            logger.log(Level.WARNING, "Attempted to remove a non-existent match: {0} (key: {1})", new Object[]{match, key});
        }
    }

    private String generateKey(Match match) {
        String team1 = match.homeTeam();
        String team2 = match.awayTeam();
        return (team1.compareTo(team2) <= 0)
                ? team1 + "_" + team2
                : team2 + "_" + team1;
    }
}
