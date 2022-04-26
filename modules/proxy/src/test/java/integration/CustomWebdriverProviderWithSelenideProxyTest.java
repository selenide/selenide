package integration;

import com.codeborne.selenide.BasicAuthCredentials;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import io.github.bonigarcia.wdm.WebDriverManager;
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
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
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

    open("/basic-auth/hello", BASIC, new BasicAuthCredentials("scott", "tiger"));
    $("#greeting").shouldHave(text("Hello, scott:tiger!"));
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
      WebDriverManager.chromedriver().setup();

      ChromeOptions options = new ChromeOptions();
      if (isHeadless()) options.setHeadless(true);
      options.addArguments("--proxy-bypass-list=<-loopback>");
      return new ChromeDriver(options.merge(desiredCapabilities));
    }

    private FirefoxDriver firefox(@Nonnull Capabilities desiredCapabilities) {
      WebDriverManager.firefoxdriver().setup();

      FirefoxOptions options = new FirefoxOptions();
      if (isHeadless()) options.setHeadless(true);
      options.addPreference("network.proxy.no_proxies_on", "");
      options.addPreference("network.proxy.allow_hijacking_localhost", true);
      return new FirefoxDriver(options.merge(desiredCapabilities));
    }
  }
}
