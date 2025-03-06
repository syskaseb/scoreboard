package com.example.scoreboard;

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
        // Given: A new scoreboard instance
        Scoreboard scoreboard = new Scoreboard();

        // When: A match is started between TeamA and TeamB
        scoreboard.startMatch("TeamA", "TeamB");

        // Then: The summary should include one match with both scores at zero
        List<MatchSnapshot> summary = scoreboard.getSummary();
        assertEquals(1, summary.size(), "Summary should contain one match.");
        MatchSnapshot match = summary.getFirst();
        assertNotNull(match, "First match should not be null.");
        assertEquals("TeamA", match.homeTeam(), "Home team should be TeamA.");
        assertEquals("TeamB", match.awayTeam(), "Away team should be TeamB.");
        assertEquals(0, match.homeScore(), "Initial home score should be 0.");
        assertEquals(0, match.awayScore(), "Initial away score should be 0.");
    }

    @Test
    void should_throw_exception_when_home_team_is_null() {
        // Given: A new scoreboard instance
        Scoreboard scoreboard = new Scoreboard();

        // When & Then: Starting a match with a null home team should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch(null, "TeamB"),
                "Expected exception when home team is null.");
        assertEquals("Home team name cannot be empty.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_home_team_is_blank() {
        // Given: A new scoreboard instance
        Scoreboard scoreboard = new Scoreboard();

        // When & Then: Starting a match with a blank home team should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("   ", "TeamB"),
                "Expected exception when home team is blank.");
        assertEquals("Home team name cannot be empty.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_away_team_is_null() {
        // Given: A new scoreboard instance
        Scoreboard scoreboard = new Scoreboard();

        // When & Then: Starting a match with a null away team should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamA", null),
                "Expected exception when away team is null.");
        assertEquals("Away team name cannot be empty.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_away_team_is_blank() {
        // Given: A new scoreboard instance
        Scoreboard scoreboard = new Scoreboard();

        // When & Then: Starting a match with a blank away team should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamA", "  "),
                "Expected exception when away team is blank.");
        assertEquals("Away team name cannot be empty.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_same_team_names_are_used() {
        // Given: A new scoreboard instance
        Scoreboard scoreboard = new Scoreboard();

        // When & Then: Starting a match with the same team for home and away should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamA", "TeamA"),
                "Expected exception when same team names are used.");
        assertEquals("Home and away teams must be different.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_duplicate_match_in_any_order() {
        // Given: A scoreboard with an ongoing match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When & Then: Starting a match with the reversed team order should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamB", "TeamA"),
                "Expected exception when duplicate match is added in reversed order.");
        assertEquals("One of the teams is already in a match.", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_team_already_in_another_match() {
        // Given: A scoreboard with an ongoing match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When & Then: Starting a match where one team is already playing should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamA", "TeamC"),
                "Expected exception when home team is already in a match.");
        assertEquals("One of the teams is already in a match.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.startMatch("TeamC", "TeamB"),
                "Expected exception when away team is already in a match.");
        assertEquals("One of the teams is already in a match.", exception.getMessage());
    }

    @Test
    void should_remove_match_when_finished() {
        // Given: A scoreboard with an ongoing match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When: Finishing the match between TeamA and TeamB
        scoreboard.finishMatch("TeamA", "TeamB");

        // Then: The summary should be empty
        List<MatchSnapshot> summary = scoreboard.getSummary();
        assertEquals(0, summary.size(), "Summary should be empty after finishing the match.");
    }

    @Test
    void should_add_multiple_matches_when_no_conflicts() {
        // Given: A new scoreboard instance
        Scoreboard scoreboard = new Scoreboard();

        // When: Multiple matches are started with no conflicting teams
        scoreboard.startMatch("TeamA", "TeamB");
        scoreboard.startMatch("TeamC", "TeamD");
        scoreboard.startMatch("TeamE", "TeamF");

        // Then: The summary should contain three matches with the correct team pairings
        List<MatchSnapshot> summary = scoreboard.getSummary();
        assertEquals(3, summary.size(), "Summary should contain three matches.");
        assertTrue(summary.stream().anyMatch(m -> m.homeTeam().equals("TeamA") && m.awayTeam().equals("TeamB")),
                "Expected match TeamA vs. TeamB to be present.");
        assertTrue(summary.stream().anyMatch(m -> m.homeTeam().equals("TeamC") && m.awayTeam().equals("TeamD")),
                "Expected match TeamC vs. TeamD to be present.");
        assertTrue(summary.stream().anyMatch(m -> m.homeTeam().equals("TeamE") && m.awayTeam().equals("TeamF")),
                "Expected match TeamE vs. TeamF to be present.");
    }

    @Test
    void should_update_score_successfully_using_match_parameter() {
        // Given: A scoreboard with an ongoing match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When: The score for the match is updated to 2-3
        scoreboard.updateScore("TeamA", "TeamB", 2, 3);

        // Then: The match snapshot reflects the updated scores
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
        // Given: A scoreboard with no match between TeamX and TeamY
        Scoreboard scoreboard = new Scoreboard();

        // When & Then: Attempting to update a non-existent match should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.updateScore("TeamX", "TeamY", 1, 1));
        assertEquals("Match not found.", exception.getMessage());
    }

    @Test
    void updateScore_should_throw_exception_for_negative_scores() {
        // Given: A scoreboard with an ongoing match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When & Then: Updating the match with negative scores should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.updateScore("TeamA", "TeamB", -1, 3));
        assertEquals("Scores must be non-negative.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> scoreboard.updateScore("TeamA", "TeamB", 2, -1));
        assertEquals("Scores must be non-negative.", exception.getMessage());
    }

    @Test
    void should_return_summary_ordered_by_total_score_and_recency() {
        // Given: A scoreboard with three ongoing matches having different scores
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("Mexico", "Canada");
        scoreboard.startMatch("Spain", "Brazil");
        scoreboard.startMatch("Germany", "France");
        scoreboard.updateScore("Mexico", "Canada", 0, 5);
        scoreboard.updateScore("Spain", "Brazil", 2, 3);
        scoreboard.updateScore("Germany", "France", 2, 1);

        // When: Retrieving the match summary
        List<MatchSnapshot> summary = scoreboard.getSummary();

        // Then: The summary is ordered by total score (descending) and recency
        assertEquals(3, summary.size(), "Summary should contain 3 matches.");
        assertEquals("Spain", summary.get(0).homeTeam());
        assertEquals("Mexico", summary.get(1).homeTeam());
        assertEquals("Germany", summary.get(2).homeTeam());
    }

    @Test
    void summary_should_be_immutable() {
        // Given: A scoreboard with an ongoing match between TeamA and TeamB
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("TeamA", "TeamB");

        // When: Retrieving the match summary
        List<MatchSnapshot> summary = scoreboard.getSummary();

        // Then: The summary list should be immutable
        Match newMatch = new Match("TeamX", "TeamY", 0, 0, 0);
        assertThrows(UnsupportedOperationException.class,
                () -> summary.add(newMatch));
    }

    @Test
    void givenScoreboardSingleton_whenGetInstanceCalledRepeatedly_thenSameInstanceReturned() {
        // Given: The Scoreboard singleton
        Scoreboard instance1 = Scoreboard.getInstance();

        // When: Calling getInstance() multiple times
        Scoreboard instance2 = Scoreboard.getInstance();

        // Then: The same instance is returned on every call
        assertSame(instance1, instance2, "getInstance() should always return the same instance");
    }
}
