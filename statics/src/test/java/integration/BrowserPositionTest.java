package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Point;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

final class BrowserPositionTest extends IntegrationTest {
  @BeforeEach
  @AfterEach
  void closeBrowser() {
    assumeThat(isHeadless()).isFalse();
    closeWebDriver();
  }

  @Test
  void ableToSetBrowserPosition() {
    Configuration.browserPosition = "30x60";

    openFile("start_page.html");

    assertThat(getWebDriver().manage().window().getPosition())
      .isEqualTo(new Point(30, 60));
  }

}
