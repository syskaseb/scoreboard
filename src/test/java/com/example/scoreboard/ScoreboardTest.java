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

        // And the first match should be TeamA vs TeamB.
        Match match = summary.getFirst();
        assertNotNull(match, "First match should not be null.");
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

    @Test
    void should_throw_exception_when_duplicate_match_in_any_order() {
        Scoreboard scoreboard = new Scoreboard();
        // Start match in one order.
        scoreboard.startMatch("TeamA", "TeamB");
        // Attempt to start match with teams in reverse order.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamB", "TeamA"),
                "Expected exception when duplicate match is added in reversed order.");
        assertEquals("One of the teams is already in a match.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_team_already_in_another_match() {
        Scoreboard scoreboard = new Scoreboard();
        // Start a match with TeamA and TeamB.
        scoreboard.startMatch("TeamA", "TeamB");
        // Attempt to start another match with TeamA and TeamC.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamA", "TeamC"),
                "Expected exception when home team is already in a match.");
        assertEquals("One of the teams is already in a match.", exception.getMessage());

        // Also, attempt with TeamC and TeamB.
        exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamC", "TeamB"),
                "Expected exception when away team is already in a match.");
        assertEquals("One of the teams is already in a match.", exception.getMessage());
    }

    @Test
    void should_remove_match_when_finished() {
        // Given a scoreboard with a match
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");
        Match match = scoreboard.getSummary().getFirst();
        assertNotNull(match, "There should be a match before finishing.");

        // When finishing the match
        scoreboard.finishMatch(match);

        // Then the summary should be empty.
        List<Match> summary = scoreboard.getSummary();
        assertEquals(0, summary.size(), "Summary should be empty after finishing the match.");
    }

    @Test
    void should_add_multiple_matches_when_no_conflicts() {
        // Given an empty scoreboard
        Scoreboard scoreboard = new Scoreboard();

        // When starting multiple matches
        scoreboard.startMatch("TeamA", "TeamB");
        scoreboard.startMatch("TeamC", "TeamD");
        scoreboard.startMatch("TeamE", "TeamF");

        // Then getSummary should return all the matches.
        List<Match> summary = scoreboard.getSummary();
        assertEquals(3, summary.size(), "Summary should contain three matches.");

        // Check that each expected match is present.
        assertTrue(summary.stream().anyMatch(m -> m.homeTeam().equals("TeamA") && m.awayTeam().equals("TeamB")),
                "Expected match TeamA vs TeamB to be present.");
        assertTrue(summary.stream().anyMatch(m -> m.homeTeam().equals("TeamC") && m.awayTeam().equals("TeamD")),
                "Expected match TeamC vs TeamD to be present.");
        assertTrue(summary.stream().anyMatch(m -> m.homeTeam().equals("TeamE") && m.awayTeam().equals("TeamF")),
                "Expected match TeamE vs TeamF to be present.");
    }
}
