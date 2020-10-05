package integration;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.WebDriverProvider;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CustomWebdriverProviderWithSelenideProxyTest extends IntegrationTest {
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
    useProxy(true);
    Configuration.browser = MyWebDriverProvider.class.getName();

    open("/basic-auth/hello", AuthenticationType.BASIC, new Credentials("scott", "tiger"));
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }

  @ParametersAreNonnullByDefault
  private static class MyWebDriverProvider implements WebDriverProvider {
    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      if (browser().isChrome()) return chrome(desiredCapabilities);
      if (browser().isFirefox()) return firefox(desiredCapabilities);
      throw new IllegalStateException("Unsupported browser: " + browser().name);
    }

    private ChromeDriver chrome(DesiredCapabilities desiredCapabilities) {
      WebDriverManager.chromedriver().setup();

      ChromeOptions options = new ChromeOptions();
      if (isHeadless()) options.setHeadless(true);
      options.addArguments("--proxy-bypass-list=<-loopback>");
      options.merge(desiredCapabilities);
      return new ChromeDriver(options);
    }

    private FirefoxDriver firefox(DesiredCapabilities desiredCapabilities) {
      WebDriverManager.firefoxdriver().setup();

      FirefoxOptions options = new FirefoxOptions();
      if (isHeadless()) options.setHeadless(true);
      options.addPreference("network.proxy.no_proxies_on", "");
      options.addPreference("network.proxy.allow_hijacking_localhost", true);
      options.merge(desiredCapabilities);
      return new FirefoxDriver(options);
    }
  }
}
