# Live Football World Cup Scoreboard Library

This is a simple Java 21 library that implements a live scoreboard for the Football World Cup. The library provides a
public API for starting matches, updating scores, finishing matches, and retrieving a summary of ongoing matches.

## Features

- **Start a New Match:**  
  Begin a match with an initial score of 0-0. The library ensures that duplicate matches are not started (i.e., a team
  cannot be in more than one match).

- **Update Score:**  
  Update the score of an ongoing match by providing the match instance and new scores. Since matches are immutable,
  updating the score creates a new match instance.

- **Finish Match:**  
  Finish an ongoing match by removing it from the scoreboard.

- **Get Summary:**  
  Retrieve an immutable summary of all ongoing matches, ordered by the total score in descending order. Matches with
  equal total scores are further ordered by recency (i.e., the match that was started later appears first).

## Implementation Details

- **In-Memory Storage:**  
  Matches are stored in a `HashMap` within the repository. A canonical key is
  generated from the team names (ignoring their order) to uniquely identify a match.

- **Immutable Records:**  
  The match is represented as an immutable Java record (`Match`), ensuring thread safety and simplicity.

- **Validation:**  
  The `ScoreboardValidator` class enforces business rules such as non-null, non-blank team names, distinct teams, and
  non-negative scores.

- **Logging:**  
  Basic logging is implemented using Java’s built-in `java.util.logging` API to warn about duplicate or non-existent
  matches during update or removal operations.

- **Test-Driven Development (TDD):**  
  Comprehensive JUnit 5 tests are provided to cover all functionality, including negative score validation and summary
  ordering.

### Assumptions and further comments

- Under uncertain requirement on the API usage, I assumed that the user will control scoreboard methods using team
  names. In the future application could support using i.e. Match UUID for scoreboard operations.
- I prevented creating multiple instances of the scoreboard as the name Live Football World Cup Scoreboard indicates
  single instance. Scoreboard class is final because context doesn't assume extensions. Could have stayed open for
  extension with regard to SOLID but decided to don't give the user control over inheritance.
- API Usage is thread safe because of the not specified use case. Wanted to prevent any unexpected behavior of the API.
- For better search performance a pageable search with support on repository level could be implemented. 
- For optimising summary printing, match insertion order within Match record was introduced.
- It wasn't mentioned explicitly what the expected summary format. For simplicity, I decided to return a list of match
  snapshots as the summary. The summary is ordered by total score in descending order, and for matches with the same
  total score, the most recently started match appears first. In the future some kind of report generator could be
  implemented.
- An alternative approach to repository using Map is to use Set and implement custom equals/hashcode methods in Match
  class, but I decided to handle it explicitly in one place along with validation
- Please visit `showcase-separate-packages-java-module-info` branch for alternative approach to packaging
- Please visit `showcase-replace-repository-map-with-set` branch for alternative approach to persisting matches (not
  preferred implementation).
- `Scoreboard.startMatch` now validation cost against repository's existing matches is now O(1) at a cost loosening the
  repository's SRP principle.

## How to Run the Tests

You can run the tests using Maven or the Maven wrapper:

- **Using the Maven Wrapper on Unix/Linux/macOS:**
  ```bash
  ./mvnw test
- Using the Maven Wrapper on Windows:
  ```bash
  mvnw.cmd test
- Or directly with Maven:
  ```bash
  mvn test

## Integrating via GitHub Packages

This library is available on GitHub Packages, simplifying integration into your Maven projects.

### Step 1: Configure Repository

Add the following repository to your project's `pom.xml`:

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/syskaseb/scoreboard</url>
  </repository>
</repositories>
```

### Step 2: Add Dependency

Include the package as a dependency:

```xml
<dependency>
  <groupId>com.example.scoreboard</groupId>
  <artifactId>scoreboard</artifactId>
  <version>{replace with actual project version}</version>
</dependency>
```

### Step 3: GitHub Authentication

GitHub Packages require authentication. Update your Maven settings (`~/.m2/settings.xml`) with your credentials:

```xml
<servers>
  <server>
    <id>github</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <password>YOUR_GITHUB_TOKEN</password>
  </server>
</servers>
```

Generate a token with the `read:packages` scope from your [GitHub Settings](https://github.com/settings/tokens).

Your project is now configured and ready to use this dependency seamlessly.

## Verifying the JAR Signature

To verify the authenticity of the `scoreboard-<project_version>.jar` file, follow these steps:

1. **Import the Public Key** (if not already imported):

   ```bash
   gpg --keyserver hkps://keys.openpgp.org --recv-keys 24665407AFF840D4
   ```

2. **Verify the Signature:**

   ```bash
   gpg --verify scoreboard-<project_version>.jar.asc scoreboard-<project_version>.jar
   ```

   If the verification is successful, you will see a message similar to:

```
gpg: Signature made YYYY-MM-DD HH:MM:SS UTC using RSA key ID <KEY_ID>
gpg: Good signature from "Author Name <author@example.com>"
```

If the signature is not valid, a warning will be displayed.

2. **Check the Key Fingerprint (Optional):**
   To confirm that the key matches the expected author, check the fingerprint:
   ```bash
   gpg --fingerprint 24665407AFF840D4
   ```