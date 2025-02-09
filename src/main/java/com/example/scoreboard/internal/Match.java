package com.example.scoreboard.internal;

import com.example.scoreboard.api.MatchSnapshot;

public record Match(String homeTeam, String awayTeam, int homeScore, int awayScore) implements MatchSnapshot {
}
