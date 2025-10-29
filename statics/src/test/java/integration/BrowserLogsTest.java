package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Level;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getWebDriverLogs;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;
import static com.codeborne.selenide.WebDriverRunner.isSafari;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.openqa.selenium.logging.LogType.BROWSER;

final class BrowserLogsTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeThat(isSafari())
      .as("Safari says `UnsupportedCommandException: The command POST /session/134...BA4/se/log was not found`")
      .isFalse();
    if (hasWebDriverStarted()) {
      getWebDriverLogs(BROWSER); // clear logs
    }
    openFile("page_with_js_errors.html");
  }

  @Test
  void canGetWebDriverBrowserConsoleLogEntry() {
    $(byText("Generate JS Error")).click();
    $(byText("Trigger TypeError 1")).click();
    $(byText("Trigger TypeError 2")).click();
    $(byText("Trigger TypeError 3")).click();
    $(byText("Trigger TypeError 4")).click();
    $(byText("Trigger TypeError 5")).click();
    List<String> webDriverLogs = getWebDriverLogs(BROWSER, Level.ALL);

    assertThat(webDriverLogs).hasSize(6);

    assertThat(webDriverLogs.get(0))
      .matches("\\[.+] \\[SEVERE] http.+/page_with_js_errors\\.html 10:6 .*ReferenceError: \\$ is not defined");
    assertThat(webDriverLogs.get(1))
      .matches("\\[.+] \\[SEVERE] http.+/page_with_js_errors\\.html \\d+:\\d+.*")
      .satisfiesAnyOf(
        log -> assertThat(log).endsWith("Uncaught TypeError: Cannot set properties of undefined (setting 'value')"),
        log -> assertThat(log).endsWith("TypeError: can't access property \"value\", data.user1 is undefined")
      );
    assertThat(webDriverLogs.get(5))
      .satisfiesAnyOf(
        log -> assertThat(log).endsWith("Uncaught TypeError: Cannot set properties of undefined (setting 'value')"),
        log -> assertThat(log).endsWith("TypeError: can't access property \"value\", data.user5 is undefined")
      );
  }
}
