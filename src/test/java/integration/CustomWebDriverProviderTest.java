package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.WebDriverProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

class CustomWebDriverProviderTest extends BaseIntegrationTest {
  SelenideDriver driver = new SelenideDriver(new SelenideConfig().browser(CustomWebDriverProvider.class.getName()));

  @BeforeEach
  void setUp() {
    assumeTrue("chrome".equalsIgnoreCase(browser));
  }

  @AfterEach
  void tearDown() {
    driver.close();
  }

  @Test
  void userCanImplementAnyCustomWebdriverProvider() {
    driver.open("/autocomplete.html");

    assertThat(driver.getWebDriver()).isInstanceOf(CustomChromeDriver.class);
  }

  private static class CustomChromeDriver extends ChromeDriver {
    protected CustomChromeDriver(ChromeOptions options) {
      super(options);
    }
  }

  private static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      ChromeOptions options = new ChromeOptions();
      if (browser().isHeadless()) options.setHeadless(true);
      return new CustomChromeDriver(options);
    }
  }
}
