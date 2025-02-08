package com.example.scoreboard;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        // Add a match.
        Match match1 = new Match("TeamA", "TeamB", 0, 0);
        repository.addMatch(match1);

        // Attempt to add a duplicate match with the teams reversed.
        Match duplicateMatch = new Match("TeamB", "TeamA", 0, 0);
        repository.addMatch(duplicateMatch);

        // Look for a warning log record.
        List<LogRecord> records = logHandler.getRecords();
        boolean foundWarning = records.stream().anyMatch(record ->
                record.getLevel().equals(Level.WARNING)
                && record.getMessage().contains("Attempted to add duplicate match"));
        assertTrue(foundWarning, "Expected warning log message when duplicate match is added.");
    }

    @Test
    void removeMatch_should_log_warning_when_non_existent_match_removed() {
        // Create a match but do not add it to the repository.
        Match nonExistentMatch = new Match("TeamA", "TeamB", 0, 0);
        repository.removeMatch(nonExistentMatch);

        // Look for a warning log record.
        List<LogRecord> records = logHandler.getRecords();
        boolean foundWarning = records.stream().anyMatch(record ->
                record.getLevel().equals(Level.WARNING)
                && record.getMessage().contains("Attempted to remove a non-existent match"));
        assertTrue(foundWarning, "Expected warning log message when removing a non-existent match.");
    }

    /**
     * A simple custom log handler that collects log records.
     */
    private static class TestLogHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
            // No action needed for flushing.
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
