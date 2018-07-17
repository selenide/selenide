package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Point;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;

class BrowserPositionTest extends IntegrationTest {
  @BeforeEach
  @AfterEach
  void closeBrowser() {
    close();
  }

  @Test
  void ableToSetBrowserPosition() {
    Assumptions.assumeFalse(isHeadless());
    Configuration.browserPosition = "30x60";

    open("/start_page.html");

    assertThat(getWebDriver().manage().window().getPosition())
      .isEqualTo(new Point(30, 60));
  }

  @Test
  void anotherBrowserPosition() {
    Assumptions.assumeFalse(isHeadless());
    Configuration.browserPosition = "110x100";

    open("/start_page.html");

    assertThat(getWebDriver().manage().window().getPosition())
      .isEqualTo(new Point(110, 100));
  }
}
