package integration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

final class UserAgentTest extends ITest {
  @Test
  void currentUserAgentTest() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    driver().open("/start_page.html");
    String userAgent = driver().getUserAgent();

    assertThat(userAgent).isNotBlank();
    assertThat(userAgent)
      .withFailMessage(String.format("Current user agent [%s] should belong to '%s' browser", userAgent, browser))
      .containsIgnoringCase(browser.substring(0, browser.length() - 1));
  }
}
