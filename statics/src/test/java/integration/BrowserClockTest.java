package integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.clock;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isSafari;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

final class BrowserClockTest extends IntegrationTest {
  private static final Instant FIXED_INSTANT = Instant.parse("2025-01-15T14:00:00Z");
  private static final long FIXED_MILLIS = FIXED_INSTANT.toEpochMilli();

  @BeforeEach
  void setUp() {
    open();
  }

  @AfterEach
  void tearDown() {
    clock().reset();
  }

  @Test
  void emulatesFixedTimeAndTimezoneForRealPageScripts() {
    // Uses CDP in Chrome/Edge, and WebDriver BiDi in Firefox - this test runs against both.
    // Verified via the page's own <script> tag (real page realm), not executeJavaScript():
    // on Firefox, a script run via classic executeScript() sees `window.Date` correctly but
    // not the bare `Date` identifier, which real page scripts (and this fixture) always use.
    assumeThat(isChrome() || isEdge() || isFirefox()).isTrue();

    clock().setTimezone("America/New_York");
    clock().setFixedTime(FIXED_INSTANT);
    openFile("page_with_clock.html");

    $("#date-now").shouldHave(text(String.valueOf(FIXED_MILLIS)));
    $("#timezone").shouldHave(text("America/New_York"));
    $("#new-york-hour").shouldHave(text("09"));
  }

  @Test
  void mocksDateApiSurface() {
    assumeThat(isChrome() || isEdge() || isFirefox()).isTrue();

    clock().setTimezone("America/New_York");
    clock().setFixedTime(FIXED_INSTANT);
    openFile("empty.html");

    // Deliberately using `window.Date`, not a bare `Date` reference: executeJavaScript() runs in
    // its own sandbox on Firefox, where a bare `Date` identifier doesn't resolve to the page's
    // mocked one - see emulatesFixedTimeAndTimezoneForRealPageScripts() for real page scripts.
    Long now = executeJavaScript("return window.Date.now();");
    Long currentDate = executeJavaScript("return new window.Date().getTime();");
    Long explicitDate = executeJavaScript("return new window.Date(1600000000000).getTime();");
    Long parsedDate = executeJavaScript("return window.Date.parse('2025-01-15T14:00:00Z');");
    Long utcDate = executeJavaScript("return window.Date.UTC(2025, 0, 15, 14, 0, 0);");
    Boolean isInstance = executeJavaScript("return new window.Date() instanceof window.Date;");
    Boolean hasMockedConstructor = executeJavaScript("return new window.Date().constructor === window.Date;");
    Boolean inheritsDatePrototype = executeJavaScript(
      "return Object.getPrototypeOf(new window.Date()) === window.Date.prototype;");
    String dateWithoutNew = executeJavaScript("return window.Date();");

    assertThat(now).isEqualTo(FIXED_MILLIS);
    assertThat(currentDate).isEqualTo(FIXED_MILLIS);
    assertThat(explicitDate).isEqualTo(1600000000000L);
    assertThat(parsedDate).isEqualTo(FIXED_MILLIS);
    assertThat(utcDate).isEqualTo(FIXED_MILLIS);
    assertThat(isInstance).isTrue();
    assertThat(hasMockedConstructor).isTrue();
    assertThat(inheritsDatePrototype).isTrue();
    assertThat(dateWithoutNew).contains("2025");
  }

  @Test
  void resetRemovesEmulationFromTheNextPage() {
    assumeThat(isChrome() || isEdge() || isFirefox()).isTrue();

    String defaultTimeZone = currentTimeZone();
    clock().setTimezone("America/New_York");
    clock().setFixedTime(FIXED_INSTANT);
    openFile("page_with_clock.html");
    $("#date-now").shouldHave(text(String.valueOf(FIXED_MILLIS)));

    clock().reset();
    openFile("page_with_clock.html");

    $("#date-now").shouldNotHave(text(String.valueOf(FIXED_MILLIS)));
    assertThat(currentTimeZone()).isEqualTo(defaultTimeZone);
  }

  @Test
  @SuppressWarnings("DataFlowIssue")
  void setTimezone_throwsNullPointerExceptionOnNullTimezoneId() {
    assertThatThrownBy(() -> clock().setTimezone(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessageContaining("timezoneId");
  }

  @Test
  void setTimezone_throwsUnsupportedOperationExceptionOnNonCdpDriver() {
    assumeThat(isSafari()).isTrue();
    assertThatThrownBy(() -> clock().setTimezone("America/New_York"))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageStartingWith("Browser clock emulation is not supported in Safari");
  }

  private String currentTimeZone() {
    return requireNonNull(executeJavaScript("return Intl.DateTimeFormat().resolvedOptions().timeZone;"));
  }
}
