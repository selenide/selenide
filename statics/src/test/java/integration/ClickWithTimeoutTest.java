package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.ClickOptions.withTimeout;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

/**
 * Click a link which loads a very slow page.
 * Link with id="slow-link" loads a page with 1.5 seconds.
 */
final class ClickWithTimeoutTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_link_to_slow_page.html");
    Configuration.timeout = 100;
    Configuration.pageLoadTimeout = 200;
  }

  @Test
  void canClickSlowLink() {
    $("#slow-link").click(usingDefaultMethod().timeout(ofSeconds(2)));
    $("h1").shouldHave(exactText("Selenide"), ofMillis(1_900));
  }

  @Test
  void canClickSlowLink_withJavascript() {
    $("#slow-link").click(usingJavaScript().timeout(ofSeconds(2)));
    $("h1").shouldHave(exactText("Selenide"), ofMillis(1_900));
  }

  @Test
  void canClickLinkWhichAppearsLongerThanStandardTimeout() {
    $("#slow-hidden-link").click(usingJavaScript().timeout(ofSeconds(2)));
    $("h1").shouldHave(exactText("Selenide"), ofMillis(1_900));
  }

  @Test
  void canClickWithCustomTimeout() {
    $("#slow-hidden-link").click(withTimeout(ofSeconds(2)));
    $("h1").shouldHave(exactText("Selenide"), ofMillis(1_900));
  }
}
