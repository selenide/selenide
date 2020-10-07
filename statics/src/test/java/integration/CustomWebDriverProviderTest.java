package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class CustomWebDriverProviderTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeTrue("chrome".equalsIgnoreCase(browser));
    Configuration.browser = CustomWebDriverProvider.class.getName();
    closeWebDriver();
  }

  @AfterEach
  void tearDown() {
    closeWebDriver();
  }

  @Test
  void userCanImplementAnyCustomWebdriverProvider() {
    open("/autocomplete.html");

    assertThat(WebDriverRunner.getWebDriver()).isInstanceOf(CustomChromeDriver.class);
  }

  private static class CustomChromeDriver extends ChromeDriver {
    protected CustomChromeDriver(ChromeOptions options) {
      super(options);
    }
  }

  private static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver createDriver(@Nonnull DesiredCapabilities desiredCapabilities) {
      ChromeOptions options = new ChromeOptions();
      if (browser().isHeadless()) options.setHeadless(true);
      return new CustomChromeDriver(options);
    }
  }
}
