package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class CloseWebdriverTest extends IntegrationTest {
  @BeforeEach
  void givenNoOpenedBrowsers() {
    assumeTrue(isChrome());
    close();
  }

  @Test
  void canCloseWebdriver_createdByHimself() {
    ChromeDriver driver = new ChromeDriver();
    WebDriverRunner.setWebDriver(driver);

    open();
    assertThat(WebDriverRunner.getWebDriver()).isSameAs(driver);

    close();
    assertThatThrownBy(WebDriverRunner::getWebDriver)
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread");
  }

  @Test
  void canCloseWebdriver_createdBySelenide() {
    open();
    assertThat(WebDriverRunner.getWebDriver()).isNotNull();

    close();
    assertThatThrownBy(WebDriverRunner::getWebDriver)
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread");
  }
}
