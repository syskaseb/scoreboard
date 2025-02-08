package com.example.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MatchRepository {

    private final Map<String, Match> matches = new HashMap<>();

    void addMatch(Match match) {
        String key = generateKey(match);
        if (matches.containsKey(key)) {
            throw new IllegalArgumentException("A match between these teams already exists.");
        }
        matches.put(key, match);
    }

    List<Match> getAllMatches() {
        return List.copyOf(matches.values());
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
