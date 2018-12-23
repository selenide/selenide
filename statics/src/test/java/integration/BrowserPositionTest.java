package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Point;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class BrowserPositionTest extends IntegrationTest {
  @BeforeEach
  @AfterEach
  void closeBrowser() {
    assumeFalse(isHeadless());
    close();
  }

  @Test
  void ableToSetBrowserPosition() {
    Configuration.browserPosition = "30x60";

    openFile("start_page.html");

    assertThat(getWebDriver().manage().window().getPosition())
      .isEqualTo(new Point(30, 60));
  }

}
