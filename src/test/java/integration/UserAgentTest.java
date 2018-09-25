package integration;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class UserAgentTest extends ITest {
  @Test
  void currentUserAgentTest() {
    Assumptions.assumeFalse(browser().isHtmlUnit());

    driver().open("/start_page.html");
    String userAgent = driver().getUserAgent();

    assertThat(userAgent)
      .isNotBlank();
    assertThat(userAgent)
      .withFailMessage(String.format("Current user agent [%s] should belong to '%s' browser", userAgent, browser))
      .containsIgnoringCase(browser);
  }
}
