package com.example.scoreboard;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScoreboardValidatorTest {

    private final ScoreboardValidator validator = new ScoreboardValidator();

    @ParameterizedTest(name = "homeTeam={0}, awayTeam={1} should throw: {2}")
    @MethodSource("invalidMatchProvider")
    void should_throw_exception_for_invalid_inputs(String homeTeam, String awayTeam, String expectedMessage) {
        // Given: Invalid match inputs (either null, blank, or identical team names)

        // When & Then: validateNewMatch should throw an IllegalArgumentException with the expected message
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateNewMatch(homeTeam, awayTeam),
                "Expected exception for invalid inputs.");
        assertEquals(expectedMessage, exception.getMessage());
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

    @ParameterizedTest(name = "homeTeam={0}, awayTeam={1} should be valid")
    @MethodSource("validMatchProvider")
    void should_not_throw_exception_when_inputs_are_valid(String homeTeam, String awayTeam) {
        // Given: Valid match inputs

        // When & Then: validateNewMatch should not throw any exception
        assertDoesNotThrow(() -> validator.validateNewMatch(homeTeam, awayTeam),
                "Should not throw exception when inputs are valid.");
    }

    static Stream<Arguments> validMatchProvider() {
        return Stream.of(
                Arguments.of("TeamA", "TeamB"),
                Arguments.of("TeamC", "TeamD")
        );
    }
}
