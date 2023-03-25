package integration;

import com.codeborne.selenide.BasicAuthCredentials;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.AuthenticationType.BASIC;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CustomWebdriverProviderWithSelenideProxyTest extends ProxyIntegrationTest {
  @BeforeEach
  void setUp() {
    assumeThat(isChrome() || isFirefox()).isTrue();
    closeWebDriver();
  }

  @AfterEach
  void tearDown() {
    closeWebDriver();
  }

  @Test
  public void userCanUserCustomWebdriverWithSelenideProxy() {
    Configuration.browser = MyWebDriverProvider.class.getName();

    open("/basic-auth/hello", BASIC, new BasicAuthCredentials(domain(), "scott", scottPassword()));
    $("#greeting").shouldHave(text("Hello, scott:" + scottPassword()));
  }

  @ParametersAreNonnullByDefault
  private static class MyWebDriverProvider implements WebDriverProvider {
    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
      if (browser().isChrome()) return chrome(capabilities);
      if (browser().isFirefox()) return firefox(capabilities);
      throw new IllegalStateException("Unsupported browser: " + browser().name);
    }

    private ChromeDriver chrome(@Nonnull Capabilities desiredCapabilities) {
      ChromeOptions options = chromeOptions(null);
      return new ChromeDriver(options.merge(desiredCapabilities));
    }

    private FirefoxDriver firefox(@Nonnull Capabilities desiredCapabilities) {
      FirefoxOptions options = firefoxOptions(null);
      return new FirefoxDriver(options.merge(desiredCapabilities));
    }
  }
}
