package integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

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
  void emulatesTimezoneAndFixedTime() {
    // Uses CDP in Chrome/Edge, and WebDriver BiDi in Firefox - this test runs against both.
    assumeThat(isChrome() || isEdge() || isFirefox()).isTrue();

    String defaultTimeZone = currentTimeZone();

    clock().setTimezone("America/New_York");
    clock().setFixedTime(FIXED_INSTANT);
    openFile("empty.html");

    Long now = executeJavaScript("return Date.now();");
    Long currentDate = executeJavaScript("return new Date().getTime();");
    Long explicitDate = executeJavaScript("return new Date(1600000000000).getTime();");
    Long parsedDate = executeJavaScript("return Date.parse('2025-01-15T14:00:00Z');");
    Long utcDate = executeJavaScript("return Date.UTC(2025, 0, 15, 14, 0, 0);");
    Boolean isInstance = executeJavaScript("return new Date() instanceof Date;");
    Boolean hasMockedConstructor = executeJavaScript("return new Date().constructor === Date;");
    Boolean inheritsDatePrototype = executeJavaScript("return Object.getPrototypeOf(new Date()) === Date.prototype;");
    String newYorkHour = executeJavaScript(
      "return new Intl.DateTimeFormat('en-US', {timeZone: 'America/New_York', hour: '2-digit', hour12: false})" +
        ".format(Date.now());"
    );
    String timeZone = currentTimeZone();
    String dateWithoutNew = executeJavaScript("return Date();");

    assertThat(now).isEqualTo(FIXED_MILLIS);
    assertThat(currentDate).isEqualTo(FIXED_MILLIS);
    assertThat(explicitDate).isEqualTo(1600000000000L);
    assertThat(parsedDate).isEqualTo(FIXED_MILLIS);
    assertThat(utcDate).isEqualTo(FIXED_MILLIS);
    assertThat(isInstance).isTrue();
    assertThat(hasMockedConstructor).isTrue();
    assertThat(inheritsDatePrototype).isTrue();
    assertThat(newYorkHour).isEqualTo("09");
    assertThat(timeZone).isEqualTo("America/New_York");
    assertThat(dateWithoutNew).contains("2025");

    clock().reset();
    openFile("empty.html");

    Long resetNow = executeJavaScript("return Date.now();");
    assertThat(resetNow).isNotEqualTo(FIXED_MILLIS);
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
