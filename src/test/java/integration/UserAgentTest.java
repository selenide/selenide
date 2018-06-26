package integration;

import com.codeborne.selenide.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.getUserAgent;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

class UserAgentTest extends IntegrationTest {
  @Test
  void currentUserAgentTest() {
    Assumptions.assumeFalse(isHtmlUnit());

    open("/start_page.html");
    String userAgent = getUserAgent();
    String browser = Configuration.browser;

    MatcherAssert.assertThat(userAgent, not(isEmptyOrNullString()));
    Assertions.assertTrue(
      StringUtils.containsIgnoreCase(userAgent, browser),
      String.format("Current user agent [%s] should belong to '%s' browser", userAgent, browser));
  }
}
