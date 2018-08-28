package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class CustomWebDriverProviderTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeTrue(isChrome());
    close();
  }

  @AfterEach
  void tearDown() {
    close();
  }

  @Test
  void userCanImplementAnyCustomWebdriverProvider() {
    Configuration.browser = CustomWebDriverProvider.class.getName();

    openFile("autocomplete.html");

    assertThat(WebDriverRunner.getWebDriver())
      .isInstanceOf(CustomChromeDriver.class);
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
