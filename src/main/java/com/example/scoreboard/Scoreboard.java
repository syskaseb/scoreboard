package com.example.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages live matches on the scoreboard.
 */
public class Scoreboard {

    private final Map<String, Match> matches = new HashMap<>();

    /**
     * Starts a match with an initial score of 0-0.
     *
     * @param homeTeam name of the home team
     * @param awayTeam name of the away team
     */
    public void startMatch(String homeTeam, String awayTeam) {
        String key = generateKey(homeTeam, awayTeam);
        // For now, all matches are added with order set to 0.
        matches.put(key, new Match(homeTeam, awayTeam, 0, 0));
    }

    /**
     * Returns an immutable summary of the current matches.
     *
     * @return an unmodifiable list of matches on the scoreboard
     */
    public List<Match> getSummary() {
        return List.copyOf(matches.values());
    }

    private String generateKey(String homeTeam, String awayTeam) {
        return homeTeam + "_" + awayTeam;
    }
}
