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

import static org.junit.jupiter.api.Assertions.*;

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
        // Given a match added to the repository
        repository.addMatch("TeamA", "TeamB", 0, 0);

        // When attempting to add a duplicate match with teams reversed
        repository.addMatch("TeamB", "TeamA", 0, 0);

        // Then a warning log message should be captured
        List<LogRecord> records = logHandler.getRecords();
        boolean foundWarning = records.stream().anyMatch(r ->
                r.getLevel().equals(Level.WARNING)
                        && r.getMessage().contains("Attempted to add duplicate match")
        );
        assertTrue(foundWarning, "Expected warning log message when duplicate match is added.");
    }

    @Test
    void removeMatch_should_log_warning_when_non_existent_match_removed() {
        // Given a match that was never added
        // When attempting to remove this non-existent match
        repository.removeMatch("TeamA", "TeamB");

        // Then a warning log message should be captured
        List<LogRecord> records = logHandler.getRecords();
        boolean foundWarning = records.stream().anyMatch(r ->
                r.getLevel().equals(Level.WARNING)
                        && r.getMessage().contains("Attempted to remove a non-existent match")
        );
        assertTrue(foundWarning, "Expected warning log message when removing a non-existent match.");
    }

    @Test
    void updateMatchScore_should_update_successfully() {
        // Given a match added to the repository
        repository.addMatch("TeamA", "TeamB", 0, 0);

        // When updating the match's score
        repository.updateMatchScore("TeamA", "TeamB", 2, 3);

        // Then the repository should return the updated match
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
        // Given a match that is not in the repository
        // When attempting to update the score for this non-existent match
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repository.updateMatchScore("TeamX", "TeamY", 1, 1),
                "Expected exception when updating non-existent match.");
        assertEquals("Match not found.", exception.getMessage());

        // Then a warning log message should be captured
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
            // No action needed
        }

        @Override
        public void close() throws SecurityException {
            records.clear();
        }

        public List<LogRecord> getRecords() {
            return records;
        }
    }
}
