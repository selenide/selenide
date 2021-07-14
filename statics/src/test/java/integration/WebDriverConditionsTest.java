package integration;

import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.ex.ConditionMetException;
import com.codeborne.selenide.ex.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.currentFrameUrl;
import static com.codeborne.selenide.WebDriverConditions.currentFrameUrlContaining;
import static com.codeborne.selenide.WebDriverConditions.currentFrameUrlStartingWith;
import static com.codeborne.selenide.WebDriverConditions.url;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static com.codeborne.selenide.WebDriverConditions.urlStartingWith;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class WebDriverConditionsTest extends IntegrationTest {
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
  void userCanDefineCustomConditions() {
    webdriver().shouldHave(tabs(1));
  }

  private ObjectCondition<WebDriver> tabs(int expectedTabsCount) {
    return new ObjectCondition<WebDriver>() {
      @Nonnull
      @Override
      public String description() {
        return "should have " + expectedTabsCount + " tabs";
      }

      @Nonnull
      @Override
      public String negativeDescription() {
        return "should not have " + expectedTabsCount + " tabs";
      }

      @Override
      public boolean test(WebDriver webdriver) {
        return webdriver.getWindowHandles().size() == expectedTabsCount;
      }

      @Nonnull
      @Override
      public String actualValue(WebDriver webdriver) {
        return String.valueOf(webdriver.getWindowHandles().size());
      }

      @Nonnull
      @Override
      public String describe(WebDriver object) {
        return "webdriver";
      }
    };
  }
}
