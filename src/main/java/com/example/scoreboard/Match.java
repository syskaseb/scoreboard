package com.example.scoreboard;

record Match(String homeTeam, String awayTeam, int homeScore, int awayScore) implements MatchSnapshot {
}
