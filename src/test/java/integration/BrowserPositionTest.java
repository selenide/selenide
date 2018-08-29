package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Point;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class BrowserPositionTest extends IntegrationTest {
  @BeforeEach
  @AfterEach
  void closeBrowser() {
    close();
    Configuration.headless = false;
  }

  @Test
  void ableToSetBrowserPosition() {
    assumeFalse(isHeadless());
    Configuration.browserPosition = "30x60";

    open("/start_page.html");

    assertThat(getWebDriver().manage().window().getPosition())
      .isEqualTo(new Point(30, 60));
  }

  @Test
  void anotherBrowserPosition() {
    assumeFalse(isHeadless());
    Configuration.browserPosition = "110x100";

    open("/start_page.html");

    assertThat(getWebDriver().manage().window().getPosition())
      .isEqualTo(new Point(110, 100));
  }
}
