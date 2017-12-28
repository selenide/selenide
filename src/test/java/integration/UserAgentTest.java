package integration;

import com.codeborne.selenide.Configuration;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.getUserAgent;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static integration.UserAgentTest.CaseInsensitiveSubstringMatcher.containsIgnoringCase;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;

public class UserAgentTest extends IntegrationTest {
  @Test
  public void currentUserAgentTest() {
    assumeFalse(isHtmlUnit());

    open("/start_page.html");
    String userAgent = getUserAgent();

    assertThat(userAgent, not(isEmptyOrNullString()));
    assertThat(userAgent, containsIgnoringCase(Configuration.browser));
  }

  public static class CaseInsensitiveSubstringMatcher extends TypeSafeMatcher<String> {
    private final String subString;

    private CaseInsensitiveSubstringMatcher(final String subString) {
      this.subString = subString;
    }

    static Matcher<String> containsIgnoringCase(final String subString) {
      return new CaseInsensitiveSubstringMatcher(subString);
    }

    @Override
    protected boolean matchesSafely(final String actualString) {
      return actualString.toLowerCase().contains(this.subString.toLowerCase());
    }

    @Override
    public void describeTo(final Description description) {
      description.appendText("containing substring \"" + this.subString + "\"");
    }
  }
}
