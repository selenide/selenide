package integration;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.StaticConfig;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CustomWebdriverWithSelenideProxyTest extends IntegrationTest {

  @BeforeEach
  void setUp() {
    assumeThat(isChrome() || isFirefox()).isTrue();
    closeWebDriver();
  }

  @Test
  public void userCanUserCustomWebdriverWithSelenideProxy() {
    useProxy(true);

    SelenideProxyServer proxy = new SelenideProxyServer(new StaticConfig(), null);
    proxy.start();
    try {
      WebDriver webDriver = isChrome() ? chrome(proxy) : firefox(proxy);
      try {
        WebDriverRunner.setWebDriver(webDriver, proxy);

        open("/basic-auth/hello", AuthenticationType.BASIC, new Credentials("scott", "tiger"));
        $("body").shouldHave(text("Hello, scott:tiger!"));
      }
      finally {
        closeWebDriver();
        webDriver.quit();
      }
    }
    finally {
      proxy.shutdown();
    }
  }

  private ChromeDriver chrome(SelenideProxyServer proxy) {
    WebDriverManager.chromedriver().setup();

    ChromeOptions options = new ChromeOptions();
    if (isHeadless()) options.setHeadless(true);
    options.setProxy(proxy.createSeleniumProxy());
    addSslErrorIgnoreCapabilities(options);
    options.addArguments("--proxy-bypass-list=<-loopback>");
    return new ChromeDriver(options);
  }

  private FirefoxDriver firefox(SelenideProxyServer proxy) {
    WebDriverManager.firefoxdriver().setup();

    FirefoxOptions options = new FirefoxOptions();
    if (isHeadless()) options.setHeadless(true);
    options.setProxy(proxy.createSeleniumProxy());
    options.addPreference("network.proxy.no_proxies_on", "");
    options.addPreference("network.proxy.allow_hijacking_localhost", true);
    return new FirefoxDriver(options);
  }
}
