package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.getUserAgent;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;

class UserAgentTest extends IntegrationTest {
  @Test
  void currentUserAgentTest() {
    Assumptions.assumeFalse(isHtmlUnit());

    open("/start_page.html");
    String userAgent = getUserAgent();
    String browser = Configuration.browser;

    assertThat(userAgent)
      .isNullOrEmpty();
    assertThat(userAgent)
      .withFailMessage(String.format("Current user agent [%s] should belong to '%s' browser", userAgent, browser))
      .isEqualToIgnoringCase(browser);
  }
}
