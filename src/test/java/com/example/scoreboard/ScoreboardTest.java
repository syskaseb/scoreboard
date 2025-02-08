package com.example.scoreboard;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScoreboardTest {

    @Test
    void should_add_match_with_zero_score_when_match_started() {
        // Given an empty scoreboard
        Scoreboard scoreboard = new Scoreboard();

        // When starting a match between TeamA and TeamB
        scoreboard.startMatch("TeamA", "TeamB");

        // Then getSummary should return one match with initial 0-0 scores.
        List<Match> summary = scoreboard.getSummary();
        assertEquals(1, summary.size(), "Summary should contain one match.");

        Match match = summary.getFirst();
        assertEquals("TeamA", match.homeTeam(), "Home team should be TeamA.");
        assertEquals("TeamB", match.awayTeam(), "Away team should be TeamB.");
        assertEquals(0, match.homeScore(), "Initial home score should be 0.");
        assertEquals(0, match.awayScore(), "Initial away score should be 0.");
    }

    @Test
    void should_throw_exception_when_home_team_is_null() {
        Scoreboard scoreboard = new Scoreboard();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch(null, "TeamB"),
                "Expected exception when home team is null.");
        assertEquals("Home team name cannot be empty.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_home_team_is_blank() {
        Scoreboard scoreboard = new Scoreboard();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("   ", "TeamB"),
                "Expected exception when home team is blank.");
        assertEquals("Home team name cannot be empty.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_away_team_is_null() {
        Scoreboard scoreboard = new Scoreboard();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamA", null),
                "Expected exception when away team is null.");
        assertEquals("Away team name cannot be empty.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_away_team_is_blank() {
        Scoreboard scoreboard = new Scoreboard();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamA", "  "),
                "Expected exception when away team is blank.");
        assertEquals("Away team name cannot be empty.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_same_team_names_are_used() {
        Scoreboard scoreboard = new Scoreboard();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamA", "TeamA"),
                "Expected exception when same team names are used.");
        assertEquals("Home and away teams must be different.", exception.getMessage());
    }
}
