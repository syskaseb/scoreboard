package com.example.scoreboard.api;

import com.example.scoreboard.internal.Match;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScoreboardTest {

    @Test
    void should_add_match_with_zero_score_when_match_started() {
        // Given an empty scoreboard
        Scoreboard scoreboard = new Scoreboard();

        // When starting a match between TeamA and TeamB
        scoreboard.startMatch("TeamA", "TeamB");

        // Then getSummary should return one match with initial 0-0 scores.
        List<MatchSnapshot> summary = scoreboard.getSummary();
        assertEquals(1, summary.size(), "Summary should contain one match.");

        // And the first match should be TeamA vs TeamB.
        MatchSnapshot match = summary.getFirst();
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

        // When finishing the match
        scoreboard.finishMatch("TeamA", "TeamB");

        // Then the summary should be empty
        List<MatchSnapshot> summary = scoreboard.getSummary();
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
        List<MatchSnapshot> summary = scoreboard.getSummary();
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

        // When updating the score using team names
        scoreboard.updateScore("TeamA", "TeamB", 2, 3);

        // Then the updated match should reflect new scores
        List<MatchSnapshot> summary = scoreboard.getSummary();
        assertEquals(1, summary.size());
        MatchSnapshot updated = summary.getFirst();
        assertEquals("TeamA", updated.homeTeam());
        assertEquals("TeamB", updated.awayTeam());
        assertEquals(2, updated.homeScore());
        assertEquals(3, updated.awayScore());
    }

    @Test
    void updateScore_should_throw_exception_for_non_existent_match() {
        // Given a scoreboard with no match between TeamX and TeamY
        Scoreboard scoreboard = new Scoreboard();

        // When updating a match that doesn't exist
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.updateScore("TeamX", "TeamY", 1, 1));

        // Then an exception with the message "Match not found." is thrown.
        assertEquals("Match not found.", exception.getMessage());
    }

    @Test
    void updateScore_should_throw_exception_for_negative_scores() {
        // Given a scoreboard with a match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When attempting to update the score with a negative home score,
        // then an exception should be thrown.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.updateScore("TeamA", "TeamB", -1, 3));
        assertEquals("Scores must be non-negative.", exception.getMessage());

        // And when attempting to update the score with a negative away score,
        // then an exception should also be thrown.
        exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.updateScore("TeamA", "TeamB", 2, -1));
        assertEquals("Scores must be non-negative.", exception.getMessage());
    }

    @Test
    void should_return_summary_ordered_by_total_score_and_recency() {
        // Given a new scoreboard
        Scoreboard scoreboard = new Scoreboard();

        // And several matches started in sequence
        // insertion index 0: Mexico vs Canada
        // insertion index 1: Spain vs Brazil
        // insertion index 2: Germany vs France
        scoreboard.startMatch("Mexico", "Canada");
        scoreboard.startMatch("Spain", "Brazil");
        scoreboard.startMatch("Germany", "France");

        // When updating scores so that Mexico/Canada and Spain/Brazil both have total = 5,
        // but Spain vs Brazil was started later (insertion index 1).
        scoreboard.updateScore("Mexico", "Canada", 0, 5);  // total = 5
        scoreboard.updateScore("Spain", "Brazil", 2, 3);   // total = 5
        scoreboard.updateScore("Germany", "France", 2, 1); // total = 3

        // Then the summary should list:
        // 1) Spain vs Brazil (5, more recent insertion than Mexico vs Canada)
        // 2) Mexico vs Canada (5)
        // 3) Germany vs France (3)
        List<MatchSnapshot> summary = scoreboard.getSummary();
        assertEquals(3, summary.size(), "Summary should contain 3 matches.");
        assertEquals("Spain", summary.get(0).homeTeam());
        assertEquals("Mexico", summary.get(1).homeTeam());
        assertEquals("Germany", summary.get(2).homeTeam());
    }

    @Test
    void summary_should_be_immutable() {
        // Given a new scoreboard instance with at least one match and the new match to be added
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When retrieving the summary
        List<MatchSnapshot> summary = scoreboard.getSummary();
        Match newMatch = new Match("TeamX", "TeamY", 0, 0);

        // Then attempting to modify the summary should throw an UnsupportedOperationException.
        assertThrows(
                UnsupportedOperationException.class,
                () -> summary.add(newMatch)
        );
    }

    @Test
    void givenScoreboardSingleton_whenGetInstanceCalledRepeatedly_thenSameInstanceReturned() {
        // Given the Scoreboard singleton implementation

        // When
        Scoreboard instance1 = Scoreboard.getInstance();
        Scoreboard instance2 = Scoreboard.getInstance();

        // Then
        assertSame(instance1, instance2, "getInstance() should always return the same instance");
    }
}
