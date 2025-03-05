package com.example.scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class MatchRepository {

    private static final Logger logger = Logger.getLogger(MatchRepository.class.getName());
    private final Map<String, Match> matches = new HashMap<>();
    private final Set<String> activeTeams = new HashSet<>();
    private long nextInsertionOrder = 0;

    synchronized void addMatch(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        if (isTeamInMatch(homeTeam) || isTeamInMatch(awayTeam)) {
            logger.log(Level.WARNING, "One of the teams is already in a match: {0} or {1}", new Object[]{homeTeam, awayTeam});
            throw new IllegalArgumentException("One of the teams is already in a match.");
        }
        String key = generateKey(homeTeam, awayTeam);
        Match newMatch = new Match(homeTeam, awayTeam, homeScore, awayScore, nextInsertionOrder++);
        if (matches.putIfAbsent(key, newMatch) != null) {
            logger.log(Level.WARNING, "Attempted to add duplicate match: {0} vs. {1} (key: {2})",
                    new Object[]{homeTeam, awayTeam, key});
        } else {
            activeTeams.add(homeTeam);
            activeTeams.add(awayTeam);
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
                    new Object[]{homeTeam, awayTeam, key});
            throw new IllegalArgumentException("Match not found.");
        }
        Match updated = new Match(existing.homeTeam(), existing.awayTeam(), homeScore, awayScore, existing.insertionOrder());
        matches.put(key, updated);
    }

    synchronized void removeMatch(String homeTeam, String awayTeam) {
        String key = generateKey(homeTeam, awayTeam);
        Match removed = matches.remove(key);
        if (removed == null) {
            logger.log(Level.WARNING, "Attempted to remove a non-existent match: {0} vs. {1} (key: {2})",
                    new Object[]{homeTeam, awayTeam, key});
        } else {
            activeTeams.remove(homeTeam);
            activeTeams.remove(awayTeam);
        }
    }

    synchronized boolean isTeamInMatch(String team) {
        return activeTeams.contains(team);
    }

    private String generateKey(String team1, String team2) {
        return (team1.compareTo(team2) <= 0)
                ? team1 + "_" + team2
                : team2 + "_" + team1;
    }
}
