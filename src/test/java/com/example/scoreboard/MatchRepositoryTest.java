package com.example.scoreboard;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MatchRepositoryTest {

    private MatchRepository repository;
    private TestLogHandler logHandler;
    private Logger logger;

    @BeforeEach
    void setUp() {
        repository = new MatchRepository();
        logger = Logger.getLogger(MatchRepository.class.getName());
        logHandler = new TestLogHandler();
        logger.addHandler(logHandler);
        logger.setLevel(Level.ALL);
    }

    @AfterEach
    void tearDown() {
        logger.removeHandler(logHandler);
    }

    @Test
    void addMatch_should_log_warning_when_duplicate_match_added() {
        // Given: A match repository with an existing match between TeamA and TeamB
        repository.addMatch("TeamA", "TeamB", 0, 0);

        // When: Attempting to add a duplicate match with reversed team order (TeamB vs TeamA)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repository.addMatch("TeamB", "TeamA", 0, 0));

        // Then: An exception is thrown and a warning log is recorded
        assertEquals("One of the teams is already in a match.", exception.getMessage());
        List<LogRecord> records = logHandler.getRecords();
        boolean foundWarning = records.stream().anyMatch(r ->
                r.getLevel().equals(Level.WARNING)
                        && r.getMessage().contains("One of the teams is already in a match")
        );
        assertTrue(foundWarning, "Expected warning log message when duplicate match is added.");
    }

    @Test
    void removeMatch_should_log_warning_when_non_existent_match_removed() {
        // Given: A match repository with no match between TeamA and TeamB

        // When: Attempting to remove a non-existent match between TeamA and TeamB
        repository.removeMatch("TeamA", "TeamB");

        // Then: A warning log message should be recorded
        List<LogRecord> records = logHandler.getRecords();
        boolean foundWarning = records.stream().anyMatch(r ->
                r.getLevel().equals(Level.WARNING)
                        && r.getMessage().contains("Attempted to remove a non-existent match")
        );
        assertTrue(foundWarning, "Expected warning log message when removing a non-existent match.");
    }

    @Test
    void updateMatchScore_should_update_successfully() {
        // Given: A match repository with an existing match between TeamA and TeamB (score 0-0)
        repository.addMatch("TeamA", "TeamB", 0, 0);

        // When: Updating the match score to 2-3
        repository.updateMatchScore("TeamA", "TeamB", 2, 3);

        // Then: The match score is updated accordingly
        List<Match> allMatches = repository.getAllMatches();
        assertEquals(1, allMatches.size(), "There should be exactly one match.");
        Match updated = allMatches.getFirst();
        assertEquals("TeamA", updated.homeTeam(), "Home team should remain TeamA.");
        assertEquals("TeamB", updated.awayTeam(), "Away team should remain TeamB.");
        assertEquals(2, updated.homeScore(), "Home score should be updated to 2.");
        assertEquals(3, updated.awayScore(), "Away score should be updated to 3.");
    }

    @Test
    void updateMatchScore_should_log_warning_when_non_existent_match() {
        // Given: A match repository without a match between TeamX and TeamY

        // When: Attempting to update the score for a non-existent match (TeamX vs TeamY)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repository.updateMatchScore("TeamX", "TeamY", 1, 1),
                "Expected exception when updating non-existent match.");

        // Then: An exception is thrown with the message "Match not found." and a warning log is recorded
        assertEquals("Match not found.", exception.getMessage());
        List<LogRecord> records = logHandler.getRecords();
        boolean foundWarning = records.stream().anyMatch(r ->
                r.getLevel().equals(Level.WARNING)
                        && r.getMessage().contains("Attempted to update non-existent match")
        );
        assertTrue(foundWarning, "Expected warning log message when updating non-existent match.");
    }

    private static class TestLogHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord r) {
            records.add(r);
        }

        @Override
        public void flush() {
            // no-op
        }

        @Override
        public void close() {
            records.clear();
        }

        public List<LogRecord> getRecords() {
            return records;
        }
    }
}
