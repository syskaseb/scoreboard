package com.example.scoreboard;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScoreboardValidatorTest {

    private final ScoreboardValidator validator = new ScoreboardValidator();

    @ParameterizedTest(name = "homeTeam={0}, awayTeam={1} should throw: {2}")
    @MethodSource("invalidMatchProvider")
    void should_throw_exception_for_invalid_inputs(String homeTeam, String awayTeam, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invokeValidator(homeTeam, awayTeam),
                "Expected exception for invalid inputs.");
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void invokeValidator(String homeTeam, String awayTeam) {
        validator.validateNewMatch(homeTeam, awayTeam, List.of());
    }

    static Stream<Arguments> invalidMatchProvider() {
        return Stream.of(
                Arguments.of(null, "TeamB", "Home team name cannot be empty."),
                Arguments.of("   ", "TeamB", "Home team name cannot be empty."),
                Arguments.of("TeamA", null, "Away team name cannot be empty."),
                Arguments.of("TeamA", "   ", "Away team name cannot be empty."),
                Arguments.of("TeamA", "TeamA", "Home and away teams must be different.")
        );
    }

    @ParameterizedTest(name = "existing match: TeamA vs. TeamB; new match: {0} vs. {1} should throw conflict")
    @MethodSource("conflictingMatchProvider")
    void should_throw_exception_when_team_already_in_a_match(String homeTeam, String awayTeam) {
        List<Match> existingMatches = List.of(new Match("TeamA", "TeamB", 0, 0));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateNewMatch(homeTeam, awayTeam, existingMatches),
                "Expected exception for conflicting match.");
        assertEquals("One of the teams is already in a match.", exception.getMessage());
    }

    static Stream<Arguments> conflictingMatchProvider() {
        return Stream.of(
                Arguments.of("TeamA", "TeamC"), // new homeTeam equals existing homeTeam
                Arguments.of("TeamC", "TeamB"), // new awayTeam equals existing awayTeam
                Arguments.of("TeamB", "TeamC"), // new homeTeam equals existing awayTeam
                Arguments.of("TeamC", "TeamA")  // new awayTeam equals existing homeTeam
        );
    }

    @ParameterizedTest(name = "homeTeam={0}, awayTeam={1} should be valid")
    @MethodSource("validMatchProvider")
    void should_not_throw_exception_when_no_conflict(String homeTeam, String awayTeam) {
        List<Match> existingMatches = List.of();
        assertDoesNotThrow(() -> validator.validateNewMatch(homeTeam, awayTeam, existingMatches),
                "Should not throw exception when inputs are valid and no conflict.");
    }

    static Stream<Arguments> validMatchProvider() {
        return Stream.of(
                Arguments.of("TeamA", "TeamB"),
                Arguments.of("TeamC", "TeamD")
        );
    }
}
