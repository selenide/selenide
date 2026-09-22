package integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.assertj.core.api.SoftAssertions;

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

    // DIAGNOSTIC (temporary): proves whether the preload script touched window.Date at all,
    // independently of whether the mocked value is correct.
    String dateToString = executeJavaScript("return window.Date.toString();");
    String dateName = executeJavaScript("return window.Date.name;");
    // DIAGNOSTIC (temporary): is there a realm split between the bare `Date` identifier and
    // `window.Date`, in the sandbox executeJavaScript() runs in?
    Boolean dateIdentical = executeJavaScript("return Date === window.Date;");
    // DIAGNOSTIC (temporary): what does a REAL <script> tag - running in the page's own realm,
    // not executeJavaScript()'s sandbox - see when it calls the bare `Date` identifier itself?
    executeJavaScript("""
      var s = document.createElement('script');
      s.textContent = "window.__pageDateNow = Date.now(); window.__pageDateName = Date.name;";
      document.body.appendChild(s);
      """);
    Long pageDateNow = executeJavaScript("return window.__pageDateNow;");
    String pageDateName = executeJavaScript("return window.__pageDateName;");

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

    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(dateName).as("window.Date.name (diagnostic)").isEqualTo("MockDate");
      softly.assertThat(dateToString).as("window.Date.toString() (diagnostic)").contains("MockDate");
      if (isFirefox()) {
        softly.assertThat(dateIdentical).as("Date === window.Date (diagnostic)").isFalse();
      }
      softly.assertThat(pageDateName).as("real <script> tag's Date.name (diagnostic)").isEqualTo("MockDate");
      softly.assertThat(pageDateNow).as("real <script> tag's Date.now() (diagnostic)").isEqualTo(FIXED_MILLIS);
      softly.assertThat(now).as("Date.now()").isEqualTo(FIXED_MILLIS);
      softly.assertThat(currentDate).as("new Date().getTime()").isEqualTo(FIXED_MILLIS);
      softly.assertThat(explicitDate).as("new Date(explicit).getTime()").isEqualTo(1600000000000L);
      softly.assertThat(parsedDate).as("Date.parse(...)").isEqualTo(FIXED_MILLIS);
      softly.assertThat(utcDate).as("Date.UTC(...)").isEqualTo(FIXED_MILLIS);
      softly.assertThat(isInstance).as("new Date() instanceof Date").isTrue();
      softly.assertThat(hasMockedConstructor).as("new Date().constructor === Date").isTrue();
      softly.assertThat(inheritsDatePrototype).as("Object.getPrototypeOf(new Date()) === Date.prototype").isTrue();
      softly.assertThat(newYorkHour).as("New York hour").isEqualTo("09");
      softly.assertThat(timeZone).as("timezone override").isEqualTo("America/New_York");
      softly.assertThat(dateWithoutNew).as("Date() without new").contains("2025");
    });

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
