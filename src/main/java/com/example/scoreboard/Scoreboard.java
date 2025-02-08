package com.example.scoreboard;

import java.util.List;

/**
 * Manages live matches on the scoreboard.
 */
public class Scoreboard {

    private final MatchRepository repository = new MatchRepository();

    /**
     * Starts a match with an initial score of 0-0.
     *
     * @param homeTeam name of the home team
     * @param awayTeam name of the away team
     */
    public void startMatch(String homeTeam, String awayTeam) {
        Match match = new Match(homeTeam, awayTeam, 0, 0);
        repository.addMatch(match);
    }

    /**
     * Returns an immutable summary of the current matches.
     *
     * @return an unmodifiable list of matches
     */
    public List<Match> getSummary() {
        return repository.getAllMatches();
    }
}
