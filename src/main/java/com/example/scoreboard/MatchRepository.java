package com.example.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class MatchRepository {
    private static final Logger logger = Logger.getLogger(MatchRepository.class.getName());

    private final Map<String, Match> matches = new HashMap<>();

    void addMatch(Match match) {
        String key = generateKey(match);
        if (matches.putIfAbsent(key, match) != null) {
            logger.log(Level.WARNING, "Attempted to add duplicate match: {0} (key: {1})", new Object[]{match, key});
        }
    }

    List<Match> getAllMatches() {
        return List.copyOf(matches.values());
    }

    void removeMatch(Match match) {
        String key = generateKey(match);
        Match removed = matches.remove(key);
        if (removed == null) {
            logger.log(Level.WARNING, "Attempted to remove a non-existent match: {0} (key: {1})", new Object[]{match, key});
        }
    }

    /**
     * Generates a canonical key so that the order of teams does not matter.
     */
    private String generateKey(Match match) {
        String team1 = match.homeTeam();
        String team2 = match.awayTeam();
        // Sort the team names lexicographically to form the key
        return (team1.compareTo(team2) <= 0)
                ? team1 + "_" + team2
                : team2 + "_" + team1;
    }
}
