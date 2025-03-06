package com.example.scoreboard;

record Match(String homeTeam,
             String awayTeam,
             int homeScore,
             int awayScore,
             long insertionOrder
) implements MatchSnapshot {

    @Override
    public String toString() {
        return "Match[homeTeam=" + homeTeam
                + ", awayTeam=" + awayTeam
                + ", homeScore=" + homeScore
                + ", awayScore=" + awayScore + "]";
    }
}
