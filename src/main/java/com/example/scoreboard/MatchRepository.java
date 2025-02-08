package com.example.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MatchRepository {

    private final Map<String, Match> matches = new HashMap<>();

    void addMatch(Match match) {
        matches.putIfAbsent(generateKey(match), match);
    }

    List<Match> getAllMatches() {
        return List.copyOf(matches.values());
    }

    private String generateKey(Match match) {
        return match.homeTeam() + "_" + match.awayTeam();
    }
}
