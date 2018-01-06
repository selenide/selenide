package integration;

import com.codeborne.selenide.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.getUserAgent;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class UserAgentTest extends IntegrationTest {
  @Test
  public void currentUserAgentTest() {
    assumeFalse(isHtmlUnit());

    open("/start_page.html");
    String userAgent = getUserAgent();
    String browser = Configuration.browser;

    assertThat(userAgent, not(isEmptyOrNullString()));
    assertTrue(String.format("Current user agent [%s] should belong to '%s' browser", userAgent, browser),
        StringUtils.containsIgnoreCase(userAgent, browser));
  }
}
