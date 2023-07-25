package integration;

import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.ex.ConditionMetException;
import com.codeborne.selenide.ex.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.cookie;
import static com.codeborne.selenide.WebDriverConditions.currentFrameUrl;
import static com.codeborne.selenide.WebDriverConditions.currentFrameUrlContaining;
import static com.codeborne.selenide.WebDriverConditions.currentFrameUrlStartingWith;
import static com.codeborne.selenide.WebDriverConditions.numberOfWindows;
import static com.codeborne.selenide.WebDriverConditions.title;
import static com.codeborne.selenide.WebDriverConditions.url;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static com.codeborne.selenide.WebDriverConditions.urlStartingWith;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class WebDriverConditionsTest extends IntegrationTest {
  private static final String NAME = "TEST_COOKIE";
  private static final String VALUE = "AF33892F98ABC39A";

  @BeforeEach
  void openTestPage() {
    openFile("page_with_frames_with_delays.html");
  }

  @Test
  void waitForUrl() {
    webdriver().shouldHave(url(baseUrl + "/page_with_frames.html"), ofMillis(2000));
  }

  @Test
  void errorMessageForWrongUrl() {
    assertThatThrownBy(() ->
      webdriver().shouldHave(url("page_with_frames.html"), ofMillis(10))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("webdriver should have url page_with_frames.html")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 10 ms.");
  }

  @Test
  void errorMessageWhenWebdriverShouldNotHaveUrl() {
    openFile("page_with_frames.html");
    String url = baseUrl + "/page_with_frames.html";

    assertThatThrownBy(() ->
      webdriver().shouldNotHave(urlStartingWith(url), ofMillis(11))
    )
      .isInstanceOf(ConditionMetException.class)
      .hasMessageStartingWith("webdriver should not have url starting with " + url)
      .hasMessageContaining("Actual value: " + url)
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 11 ms.");
  }

  @Test
  void waitForUrlStartingWith() {
    webdriver().shouldHave(urlStartingWith(baseUrl + "/page_with_"), ofMillis(2000));
  }

  @Test
  void waitForUrlContaining() {
    webdriver().shouldHave(urlContaining("_with_"), ofMillis(2000));
  }

  @Test
  void errorMessageForWrongUrlStartingWith() {
    assertThatThrownBy(() ->
      webdriver().shouldHave(urlStartingWith("https://google.ee/"), ofMillis(10))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("webdriver should have url starting with https://google.ee/")
      .hasMessageContaining("Actual value: " + baseUrl + "/page_with_frames_with_delays.html")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 10 ms.");
  }

  @Test
  void waitForCurrentFrameUrl() {
    webdriver().shouldHave(currentFrameUrl(baseUrl + "/page_with_frames.html"), ofMillis(2000));
  }

  @Test
  void errorMessageForWrongCurrentFrameUrl() {
    assertThatThrownBy(() ->
      webdriver().shouldHave(currentFrameUrl("https://google.ee/"), ofMillis(20))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("current frame should have url https://google.ee/")
      .hasMessageContaining("Actual value: " + baseUrl + "/page_with_frames_with_delays.html")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 20 ms.");
  }

  @Test
  void waitForUrlCurrentFrameStartingWith() {
    webdriver().shouldHave(currentFrameUrlStartingWith(baseUrl + "/page_with_"), ofMillis(2000));
  }

  @Test
  void waitForUrlCurrentFrameContaining() {
    webdriver().shouldHave(currentFrameUrlContaining("e_with_"), ofMillis(2000));
  }

  @Test
  void errorMessageForWrongCurrentFrameUrlStartingWith() {
    assertThatThrownBy(() ->
      webdriver().shouldHave(currentFrameUrlStartingWith("https://google.ee/"), ofMillis(5))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("current frame should have url starting with https://google.ee/")
      .hasMessageContaining("Actual value: " + baseUrl + "/page_with_frames_with_delays.html")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 5 ms.");
  }

  @Test
  void checkNumberOfOpenWindows() {
    openFile("page_with_tabs.html");

    webdriver().shouldHave(numberOfWindows(1));
    $(byText("Page4: same title")).click();
    webdriver().shouldHave(numberOfWindows(2));
    $(byText("Page5: same title")).click();
    webdriver().shouldHave(numberOfWindows(3));

    switchTo().window(2).close();
    webdriver().shouldHave(numberOfWindows(2));
    switchTo().window(1).close();
    webdriver().shouldHave(numberOfWindows(1));
    switchTo().window(0);
  }

  @Test
  void errorMessageForNumberOfWindows() {
    assertThatThrownBy(() ->
      webdriver().shouldHave(numberOfWindows(2)))
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageContaining("webdriver should have 2 window(s)")
      .hasMessageContaining("Actual value: 1");

    assertThatThrownBy(() ->
      webdriver().shouldNotHave(numberOfWindows(1)))
      .isInstanceOf(ConditionMetException.class)
      .hasMessageContaining("webdriver should not have 1 window(s)")
      .hasMessageContaining("Actual value: 1");
  }

  @Test
  void checkForPageTitle() {
    webdriver().shouldHave(title("Test::frames with delays"), ofMillis(10));
  }

  @Test
  void errorMessageForWrongTitle() {
    assertThatThrownBy(() ->
      webdriver().shouldHave(title("Selenide-test-page"), ofMillis(10))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageContaining("Actual value: Test::frames with delays");
  }

  @Test
  void errorMessageWhenWebdriverShouldNotHaveTitle() {
    assertThatThrownBy(() ->
      webdriver().shouldNotHave(title("Test::frames with delays"), ofMillis(10))
    )
      .isInstanceOf(ConditionMetException.class)
      .hasMessageStartingWith("Page should not have title Test::frames with delays")
      .hasMessageContaining("Actual value: Test::frames with delays");
  }

  @Test
  void userCanDefineCustomConditions() {
    webdriver().shouldHave(customCookie("session_id"));
    webdriver().shouldNotHave(customCookie("nonexistent_cookie"));
  }

  @ParametersAreNonnullByDefault
  @SuppressWarnings("AnonymousInnerClassMayBeStatic")
  private ObjectCondition<WebDriver> customCookie(String expectedCookieName) {
    return new ObjectCondition<>() {
      @Nonnull
      @Override
      public String description() {
        return "should have a cookie with name '" + expectedCookieName + "'";
      }

      @Nonnull
      @Override
      public String negativeDescription() {
        return "should not have a cookie with name '" + expectedCookieName + "'";
      }

      @CheckReturnValue
      @Override
      public boolean test(WebDriver webdriver) {
        return webdriver.manage().getCookieNamed(expectedCookieName) != null;
      }

      @Nonnull
      @Override
      public String actualValue(WebDriver webdriver) {
        return "Available cookies: " + webdriver.manage().getCookies();
      }

      @Override
      @CheckReturnValue
      public String expectedValue() {
        return expectedCookieName;
      }

      @Nonnull
      @Override
      public String describe(WebDriver object) {
        return "webdriver";
      }
    };
  }

  @Test
  void assertPresenceOfCookieWithGivenName() {
    openPageWithCookies();

    $("#button-put").click();
    webdriver().shouldHave(cookie(NAME));
  }

  @Test
  void assertPresenceOfCookieWithGivenName_errorMessage() {
    openPageWithCookies();

    $("#button-put").click();
    assertThatThrownBy(() -> webdriver().shouldHave(cookie("WRONG_COOKIE")))
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("webdriver should have a cookie with name \"WRONG_COOKIE\"")
      .hasMessageContaining("Actual value: Available cookies: [TEST_COOKIE=AF33892F98ABC39A")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void assertPresenceOfCookieWithGivenNameAndValue() {
    openPageWithCookies();

    $("#button-put").click();
    webdriver().shouldHave(cookie(NAME, VALUE));
  }

  @Test
  void assertPresenceOfCookieWithGivenNameAndValue_errorMessage() {
    openPageWithCookies();

    $("#button-put").click();
    assertThatThrownBy(() -> webdriver().shouldHave(cookie("WRONG_COOKIE", VALUE)))
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("webdriver should have a cookie with name \"WRONG_COOKIE\" and value \"AF33892F98ABC39A\"")
      .hasMessageContaining("Actual value: Available cookies: [TEST_COOKIE=AF33892F98ABC39A");
  }

  @Test
  void assertAbsenceOfCookieWithGivenName() {
    openPageWithCookies();

    addCustomCookie();
    $("#button-remove").click();
    webdriver().shouldNotHave(cookie(NAME));
  }

  @Test
  void assertAbsenceOfGivenCookieWithGivenName_errorMessage() {
    openPageWithCookies();
    addCustomCookie();
    assertThatThrownBy(() -> webdriver().shouldNotHave(cookie(NAME)))
      .isInstanceOf(ConditionMetException.class)
      .hasMessageStartingWith("webdriver should not have cookie with name \"TEST_COOKIE\"")
      .hasMessageContaining("Actual value: Available cookies: [TEST_COOKIE=AF33892F98ABC39A");
  }

  @Test
  void assertAbsenceOfCookieWithGivenNameAndValue() {
    openPageWithCookies();

    addCustomCookie();
    $("#button-remove").click();
    webdriver().shouldNotHave(cookie(NAME, VALUE));
  }

  @Test
  void assertAbsenceOfGivenCookieWithGivenNameAndValue_errorMessage() {
    openPageWithCookies();
    addCustomCookie();
    assertThatThrownBy(() -> webdriver().shouldNotHave(cookie(NAME, VALUE)))
      .isInstanceOf(ConditionMetException.class)
      .hasMessageStartingWith("webdriver should not have cookie with name \"TEST_COOKIE\" and value \"AF33892F98ABC39A\"")
      .hasMessageContaining("Actual value: Available cookies: [TEST_COOKIE=AF33892F98ABC39A");
  }

  private void openPageWithCookies() {
    openFile("cookies.html");
    clearBrowserCookies();
  }

  private void addCustomCookie() {
    webdriver().driver().getWebDriver().manage().addCookie(new Cookie(NAME, VALUE));
  }
}
