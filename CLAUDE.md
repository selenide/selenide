# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Selenide is a Java framework providing a concise fluent API for Selenium WebDriver. It simplifies writing browser-based automated tests. Current version: 7.15.1, requires Java 17+.

## Build Commands

```bash
./gradlew                              # Default: runs unit tests + publishes JARs to local Maven repo
./gradlew check                        # Run unit tests only
./gradlew publishToMavenLocal -x test  # Publish JARs without testing

# Integration tests (require a browser)
./gradlew chrome_headless              # Chrome headless integration tests
./gradlew firefox_headless             # Firefox headless integration tests
./gradlew chrome                       # Chrome integration tests (visible)
./gradlew firefox                      # Firefox integration tests (visible)

# Run a single test class
./gradlew check --tests ClassName                    # Unit test
./gradlew chrome_headless --tests integration.FooTest  # Integration test

# Full suite
./gradlew allTests   # clean + check + firefox_headless + chrome_headless
```

## Architecture

### Module Structure

The project is a multi-module Gradle build. The key relationship:

- **`modules/core`** (`selenide-core`) — All core implementation. Sources live at the root `src/main/java` and `src/test/java` but are compiled as part of the `:modules:core` subproject (see `modules/core/build.gradle` `srcDir` declarations).
- **`statics`** (`selenide`) — Static facade layer (`Selenide.java`, `WebDriverRunner.java`, `Configuration.java`). Depends on `modules/core`. This is the artifact most users depend on.
- **`modules/proxy`** — BrowserUp proxy integration for request/response interception.
- **`modules/appium`** — Mobile testing via Appium. Depends on `statics`.
- **`modules/selenoid`, `modules/moon`, `modules/grid`** — Remote browser providers.
- **`modules/junit4`, `modules/testng`** — Test framework integrations.
- **`modules/video-recorder*`** — Video recording support.
- **`modules/clear-with-shortcut`, `modules/full-screenshot`, `modules/error-message-customizer`** — Small feature modules.

All plugin modules depend on `statics` (which transitively provides `core`).

### Core Package Layout (`com.codeborne.selenide`)

- **`commands/`** — Individual command implementations (Click, Type, Clear, etc.) implementing `Command` or `FluentCommand`.
- **`conditions/`** — Condition implementations for element/collection assertions (Text, Visible, Attribute, etc.).
- **`impl/`** — Internal implementation details (WebDriver management, screenshots, window handling).
- **`ex/`** — Custom exception hierarchy.
- **`collections/`** — Collection-level condition assertions (ExactTexts, Texts, etc.).
- **`selector/`** — Custom CSS/XPath locator strategies.
- **`webdriver/`** — WebDriver factory and provider interfaces.
- **`proxy/`** — Proxy server integration interfaces.
- **`files/`** — File download management.
- **`logevents/`** — Event logging (SelenideLogger).
- **`drivercommands/`** — Driver-level operations (screenshot, refresh, close).

### Test Structure

- **Unit tests** (`src/test/java/com/codeborne/selenide/`): Run with `./gradlew check`. No browser needed.
- **Integration tests** (`src/test/java/integration/`): Require a browser. Run with browser-specific tasks (`chrome_headless`, `firefox`, etc.). 
- **Base class**: Integration tests extend `ITest` which extends `BaseIntegrationTest`. `ITest` manages a `ThreadLocal<SelenideDriver>` and `ThreadLocal<SelenideConfig>` per test. `BaseIntegrationTest` starts a local HTTPS server.
- Tests run with Turkish locale (`user.language=tr`, `user.country=TR`) to catch locale-sensitive bugs.

## Code Style

- **2-space indentation**, no tabs.
- **Max line length: 136** characters.
- **No star imports**, no unused imports, no `junit` or `org.hamcrest` imports (enforced by Checkstyle).
- **Max cyclomatic complexity: 11**.
- LF line endings, newline at end of file.
- Checkstyle (`maxWarnings = 0`) and SpotBugs (effort MAX) run as part of `check`. Zero warnings policy.

## Branching

All work on feature branches from `main`, merged back to `main`. Commit messages should start with issue ID: `#ID description`.

## Key Dependencies

- Selenium WebDriver 4.41.0
- JUnit 6.0.3 (Jupiter)
- BrowserUp Proxy 3.2.2
- Gradle 8.14.4
