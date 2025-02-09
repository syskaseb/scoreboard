package com.example.scoreboard;

import java.util.Objects;

/**
 * Immutable record representing a match.
 */
public record Match(String homeTeam, String awayTeam, int homeScore, int awayScore) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match that)) return false;

        return this.generateKey().equals(that.generateKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(generateKey());
    }

    private String generateKey() {
        return (homeTeam.compareTo(awayTeam) <= 0)
                ? homeTeam + "_" + awayTeam
                : awayTeam + "_" + homeTeam;
    }
}

