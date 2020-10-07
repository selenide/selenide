package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Level;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getWebDriverLogs;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.openqa.selenium.logging.LogType.BROWSER;

final class BrowserLogsTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeFalse(isFirefox(),
      "Firefox says `UnsupportedCommandException: POST /session/b493bc56.../log did not match a known command`"
    );

    if (hasWebDriverStarted()) {
      getWebDriverLogs(BROWSER); // clear logs
    }
    openFile("page_with_js_errors.html");
  }

  @Test
  void canGetWebDriverBrowserConsoleLogEntry() {
    $(byText("Generate JS Error")).click();
    List<String> webDriverLogs = getWebDriverLogs(BROWSER, Level.ALL);

    assertThat(webDriverLogs).hasSize(1);

    String logEntry = webDriverLogs.get(0);
    assertThat(logEntry)
      .contains("ReferenceError")
      .contains("$")
      .contains("/page_with_js_errors.html");
  }
}
