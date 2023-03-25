package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CloseWebdriverTest extends IntegrationTest {
  @BeforeEach
  void givenNoOpenedBrowsers() {
    assumeThat(isChrome() || isFirefox()).isTrue();
    closeWebDriver();
  }

  @Test
  void canCloseWebdriver_createdByHimself() {
    WebDriver driver = isFirefox() ? openFirefox() : openChrome();
    WebDriverRunner.setWebDriver(driver);

    open();
    assertThat(WebDriverRunner.getWebDriver()).isSameAs(driver);

    closeWebDriver();
    assertThatThrownBy(WebDriverRunner::getWebDriver)
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread");
  }

  @Test
  void canCloseWebdriver_createdBySelenide() {
    open();
    assertThat(WebDriverRunner.getWebDriver()).isNotNull();

    closeWebDriver();
    assertThatThrownBy(WebDriverRunner::getWebDriver)
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread");
  }
}
