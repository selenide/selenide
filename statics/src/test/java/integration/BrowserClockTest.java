package integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.codeborne.selenide.Selenide.clock;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.assertj.core.api.Assertions.assertThat;

final class BrowserClockTest extends IntegrationTest {
  private static final Instant FIXED_INSTANT = Instant.parse("2025-01-15T14:00:00Z");
  private static final long FIXED_MILLIS = FIXED_INSTANT.toEpochMilli();

  @AfterAll
  static void closeBrowser() {
    closeWebDriver();
  }

  @Test
  void emulatesTimezoneAndFixedTimeForNewDocumentsAndResetsWithoutLeak() {
    // Uses CDP in Chrome/Edge, and WebDriver BiDi in Firefox - this test runs against both.
    openFile("empty.html");
    String defaultTimeZone = currentTimeZone();

    try {
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
    finally {
      try {
        clock().reset();
      }
      finally {
        closeWebDriver();
      }
    }
  }

  private String currentTimeZone() {
    return executeJavaScript("return Intl.DateTimeFormat().resolvedOptions().timeZone;");
  }
}
