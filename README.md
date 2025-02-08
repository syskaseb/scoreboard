# Live Football World Cup Scoreboard Library

This is a simple Java library that implements a live scoreboard for the Football World Cup. The library provides a public API for starting matches, updating scores, finishing matches, and retrieving a summary of ongoing matches.

## Features

- **Start a New Match:**  
  Begin a match with an initial score of 0-0. The library ensures that duplicate matches are not started (i.e., a team cannot be in more than one match).

- **Update Score:**  
  Update the score of an ongoing match by providing the match instance and new scores. Since matches are immutable, updating the score creates a new match instance.

- **Finish Match:**  
  Finish an ongoing match by removing it from the scoreboard.

- **Get Summary:**  
  Retrieve an immutable summary of all ongoing matches, ordered by the total score in descending order. Matches with equal total scores are further ordered by recency (i.e., the match that was started later appears first).

## Implementation Details

- **In-Memory Storage:**  
  Matches are stored in a `LinkedHashMap` within the repository to preserve insertion order. A canonical key is generated from the team names (ignoring their order) to uniquely identify a match.

- **Immutable Records:**  
  The match is represented as an immutable Java record (`Match`), ensuring thread safety and simplicity.

- **Validation:**  
  The `ScoreboardValidator` class enforces business rules such as non-null, non-blank team names, distinct teams, and non-negative scores.

- **Logging:**  
  Basic logging is implemented using Java’s built-in `java.util.logging` API to warn about duplicate or non-existent matches during update or removal operations.

- **Test-Driven Development (TDD):**  
  Comprehensive JUnit 5 tests are provided to cover all functionality, including negative score validation and summary ordering.

## How to Run the Tests

You can run the tests using your favorite build tool:

- **Maven:**
  ```bash
  mvn test
