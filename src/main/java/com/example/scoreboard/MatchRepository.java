package com.example.scoreboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class MatchRepository {

    private static final Logger logger = Logger.getLogger(MatchRepository.class.getName());
    private final Map<String, Match> matches = new LinkedHashMap<>();

    synchronized void addMatch(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        String key = generateKey(homeTeam, awayTeam);
        if (matches.putIfAbsent(key, new Match(homeTeam, awayTeam, homeScore, awayScore)) != null) {
            logger.log(Level.WARNING, "Attempted to add duplicate match: {0} vs. {1} (key: {2})",
                    new Object[] { homeTeam, awayTeam, key });
        }
    }

    synchronized List<Match> getAllMatches() {
        return List.copyOf(matches.values());
    }

    synchronized void updateMatchScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        String key = generateKey(homeTeam, awayTeam);
        Match existing = matches.get(key);
        if (existing == null) {
            logger.log(Level.WARNING, "Attempted to update non-existent match: {0} vs. {1} (key: {2})",
                    new Object[] { homeTeam, awayTeam, key });
            throw new IllegalArgumentException("Match not found.");
        }
        Match updated = new Match(existing.homeTeam(), existing.awayTeam(), homeScore, awayScore);
        matches.put(key, updated);
    }

    synchronized void removeMatch(String homeTeam, String awayTeam) {
        String key = generateKey(homeTeam, awayTeam);
        Match removed = matches.remove(key);
        if (removed == null) {
            logger.log(Level.WARNING, "Attempted to remove a non-existent match: {0} vs. {1} (key: {2})",
                    new Object[] { homeTeam, awayTeam, key });
        }
    }

    private String generateKey(String team1, String team2) {
        return (team1.compareTo(team2) <= 0)
                ? team1 + "_" + team2
                : team2 + "_" + team1;
    }
}
