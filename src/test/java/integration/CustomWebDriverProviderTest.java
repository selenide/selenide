package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.isChrome;

class CustomWebDriverProviderTest extends IntegrationTest {
  private String originalWebdriver;

  @BeforeEach
  void setUp() {
    originalWebdriver = Configuration.browser;

    Assumptions.assumeTrue(isChrome());
    close();
  }

  @AfterEach
  void tearDown() {
    close();
    Configuration.browser = originalWebdriver;
  }

  @Test
  void userCanImplementAnyCustomWebdriverProvider() {
    Configuration.browser = CustomWebDriverProvider.class.getName();

    openFile("autocomplete.html");

    Assertions.assertTrue(WebDriverRunner.getWebDriver() instanceof CustomChromeDriver);
  }

  private static class CustomChromeDriver extends ChromeDriver {
  }

  private static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      return new CustomChromeDriver();
    }
  }
}
