package integration;

import com.codeborne.selenide.ex.JavaScriptErrorsFound;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.logging.Level;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class BrowserLogsTest extends IntegrationTest {
  @Before
  public void setUp() {
    getWebDriverLogs(BROWSER); // clear logs
    openFile("page_with_js_errors.html");
  }


  @Test
  public void canCheckJavaScriptErrors() {
    assumeFalse(isFirefox());  // window.onerror does not work in Firefox for unknown reason :(

    assertNoJavascriptErrors();
    $(byText("Generate JS Error")).click();
    
    assertEquals(1, getJavascriptErrors().size());

    String jsError = getJavascriptErrors().get(0);
    assertTrue(jsError, jsError.contains("ReferenceError"));
    assertTrue(jsError, jsError.contains("$"));
    assertTrue(jsError, jsError.contains("/page_with_js_errors.html"));
  }

  @Test
  public void canAssertNoJavaScriptErrors() {
    assumeFalse(isFirefox());  // window.onerror does not work in Firefox for unknown reason :(
    $(byText("Generate JS Error")).click();
    try {
      assertNoJavascriptErrors();
      fail("Expected JavaScriptErrorsFound");
    }
    catch (JavaScriptErrorsFound expected) {
      assertEquals(1, expected.getJsErrors().size());
      assertTrue(expected.getJsErrors().get(0).contains("ReferenceError"));
    }
  }

  @Test
  public void canGetWebDriverBrowserConsoleLogEntry() {
    $(byText("Generate JS Error")).click();
    List<String> webDriverLogs = getWebDriverLogs(BROWSER, Level.ALL);

    assumeFalse(isHtmlUnit() || isPhantomjs() || isFirefox() || isSafari() || isChrome());

    assertEquals("Expected 1 log, but received: " + webDriverLogs, 1, webDriverLogs.size());

    String logEntry = webDriverLogs.get(0);
    assertTrue(logEntry, logEntry.contains("ReferenceError"));
    assertTrue(logEntry, logEntry.contains("$"));
    assertTrue(logEntry, logEntry.contains("/page_with_js_errors.html"));
  }
}
