package integration;

import java.util.List;
import java.util.logging.Level;

import com.codeborne.selenide.ex.JavaScriptErrorsFound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.assertNoJavascriptErrors;
import static com.codeborne.selenide.Selenide.getJavascriptErrors;
import static com.codeborne.selenide.Selenide.getWebDriverLogs;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static com.codeborne.selenide.WebDriverRunner.isSafari;
import static org.openqa.selenium.logging.LogType.BROWSER;

class BrowserLogsTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    getWebDriverLogs(BROWSER); // clear logs
    openFile("page_with_js_errors.html");
  }

  @Test
  void canCheckJavaScriptErrors() {
    Assumptions.assumeFalse(isFirefox() || isChrome());  // window.onerror does not work in Firefox for unknown reason :(

    assertNoJavascriptErrors();
    $(byText("Generate JS Error")).click();

    Assertions.assertEquals(1, getJavascriptErrors().size());

    String jsError = getJavascriptErrors().get(0);
    Assertions.assertTrue(jsError.contains("ReferenceError"), jsError);
    Assertions.assertTrue(jsError.contains("$"), jsError);
    Assertions.assertTrue(jsError.contains("/page_with_js_errors.html"), jsError);
  }

  @Test
  void canAssertNoJavaScriptErrors() {
    Assumptions.assumeFalse(isFirefox());  // window.onerror does not work in Firefox for unknown reason :(
    $(byText("Generate JS Error")).click();
    try {
      assertNoJavascriptErrors();
      Assertions.fail("Expected JavaScriptErrorsFound");
    } catch (JavaScriptErrorsFound expected) {
      Assertions.assertEquals(1, expected.getJsErrors().size());
      Assertions.assertTrue(expected.getJsErrors().get(0).contains("ReferenceError"));
    }
  }

  @Test
  void canGetWebDriverBrowserConsoleLogEntry() {
    $(byText("Generate JS Error")).click();
    List<String> webDriverLogs = getWebDriverLogs(BROWSER, Level.ALL);

    Assumptions.assumeFalse(isHtmlUnit() || isPhantomjs() || isFirefox() || isSafari() || isChrome());

    Assertions.assertEquals(1, webDriverLogs.size(), "Expected 1 log, but received: " + webDriverLogs);

    String logEntry = webDriverLogs.get(0);
    Assertions.assertTrue(logEntry.contains("ReferenceError"), logEntry);
    Assertions.assertTrue(logEntry.contains("$"), logEntry);
    Assertions.assertTrue(logEntry.contains("/page_with_js_errors.html"), logEntry);
  }
}
