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
        // Given an empty scoreboard
        Scoreboard scoreboard = new Scoreboard();

        // When starting a match with a null home team
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch(null, "TeamB"),
                "Expected exception when home team is null.");

        // Then an exception with the appropriate message is thrown.
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
        // Given a started match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When attempting to start a match with the same teams in reverse order
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamB", "TeamA"),
                "Expected exception when duplicate match is added in reversed order.");

        // Then an exception with the appropriate message is thrown.
        assertEquals("One of the teams is already in a match.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_team_already_in_another_match() {
        // Given a scoreboard with a match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When starting another match with TeamA and TeamC
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamA", "TeamC"),
                "Expected exception when home team is already in a match.");
        assertEquals("One of the teams is already in a match.", exception.getMessage());

        // And when starting a match with TeamC and TeamB
        exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamC", "TeamB"),
                "Expected exception when away team is already in a match.");
        assertEquals("One of the teams is already in a match.", exception.getMessage());
    }

    @Test
    void should_remove_match_when_finished() {
        // Given a scoreboard with a match between TeamA and TeamB
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

        // And each expected match should be present.
        assertTrue(summary.stream().anyMatch(m -> m.homeTeam().equals("TeamA") && m.awayTeam().equals("TeamB")),
                "Expected match TeamA vs TeamB to be present.");
        assertTrue(summary.stream().anyMatch(m -> m.homeTeam().equals("TeamC") && m.awayTeam().equals("TeamD")),
                "Expected match TeamC vs TeamD to be present.");
        assertTrue(summary.stream().anyMatch(m -> m.homeTeam().equals("TeamE") && m.awayTeam().equals("TeamF")),
                "Expected match TeamE vs TeamF to be present.");
    }

    @Test
    void should_update_score_successfully_using_match_parameter() {
        // Given a scoreboard with a match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");
        Match original = scoreboard.getSummary().getFirst();

        // When updating the score using the match instance
        scoreboard.updateScore(original, 2, 3);

        // Then the updated match should reflect the new scores.
        List<Match> summary = scoreboard.getSummary();
        assertEquals(1, summary.size());
        Match updated = summary.getFirst();
        assertEquals("TeamA", updated.homeTeam());
        assertEquals("TeamB", updated.awayTeam());
        assertEquals(2, updated.homeScore());
        assertEquals(3, updated.awayScore());
    }

    @Test
    void updateScore_should_throw_exception_for_non_existent_match() {
        // Given a scoreboard with no match between TeamX and TeamY
        Scoreboard scoreboard = new Scoreboard();
        Match nonExistent = new Match("TeamX", "TeamY", 0, 0);

        // When attempting to update the score for the non-existent match
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.updateScore(nonExistent, 1, 1));

        // Then an exception with the message "Match not found." is thrown.
        assertEquals("Match not found.", exception.getMessage());
    }

    @Test
    void updateScore_should_throw_exception_for_negative_scores() {
        // Given a scoreboard with a match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");
        Match match = scoreboard.getSummary().getFirst();

        // When attempting to update the score with a negative home score,
        // then an exception should be thrown.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.updateScore(match, -1, 3));
        assertEquals("Scores must be non-negative.", exception.getMessage());

        // And when attempting to update the score with a negative away score,
        // then an exception should also be thrown.
        exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.updateScore(match, 2, -1));
        assertEquals("Scores must be non-negative.", exception.getMessage());
    }

    @Test
    void should_return_summary_ordered_by_total_score_and_recency() {
        // Given a scoreboard with several matches started in sequence
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("Mexico", "Canada");    // Insertion index 0
        scoreboard.startMatch("Spain", "Brazil");       // Insertion index 1
        scoreboard.startMatch("Germany", "France");     // Insertion index 2

        // When updating scores so that two matches have equal total scores:
        // - Mexico vs Canada: total score 5 (0+5)
        // - Spain vs Brazil: total score 5 (2+3)
        // - Germany vs France: total score 3 (2+1)
        List<Match> initialSummary = scoreboard.getSummary();
        Match mexicoMatch = initialSummary.stream()
                .filter(m -> m.homeTeam().equals("Mexico"))
                .findFirst().orElseThrow();
        Match spainMatch = initialSummary.stream()
                .filter(m -> m.homeTeam().equals("Spain"))
                .findFirst().orElseThrow();
        Match germanyMatch = initialSummary.stream()
                .filter(m -> m.homeTeam().equals("Germany"))
                .findFirst().orElseThrow();

        scoreboard.updateScore(mexicoMatch, 0, 5);
        scoreboard.updateScore(spainMatch, 2, 3);
        scoreboard.updateScore(germanyMatch, 2, 1);

        // Then the summary should be ordered as follows:
        // 1. Spain vs Brazil (total 5, more recent than Mexico vs Canada)
        // 2. Mexico vs Canada (total 5)
        // 3. Germany vs France (total 3)
        List<Match> summary = scoreboard.getSummary();
        assertEquals(3, summary.size());
        assertEquals("Spain", summary.getFirst().homeTeam());
        assertEquals("Mexico", summary.get(1).homeTeam());
        assertEquals("Germany", summary.get(2).homeTeam());
    }
}
