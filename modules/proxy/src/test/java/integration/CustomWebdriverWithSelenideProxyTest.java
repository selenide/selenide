package integration;

import com.codeborne.selenide.BasicAuthCredentials;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static com.codeborne.selenide.AuthenticationType.BASIC;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static org.assertj.core.api.Assumptions.assumeThat;

final class CustomWebdriverWithSelenideProxyTest extends ProxyIntegrationTest {

  @BeforeEach
  void setUp() {
    assumeThat(isChrome() || isFirefox()).isTrue();
    closeWebDriver();
  }

  @Test
  public void userCanUserCustomWebdriverWithSelenideProxy() {
    SelenideProxyServer proxy = new SelenideProxyServer(new SelenideConfig(), null);
    proxy.start();
    try {
      WebDriver webDriver = isChrome() ? chrome(proxy) : firefox(proxy);
      try {
        WebDriverRunner.setWebDriver(webDriver, proxy);

        open("/basic-auth/hello", BASIC, new BasicAuthCredentials("scott", "tiger"));
        $("body").shouldHave(partialText("Hello, scott:tiger!"));
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
    options.setProxy(proxy.getSeleniumProxy());
    addSslErrorIgnoreCapabilities(options);
    options.addArguments("--proxy-bypass-list=<-loopback>");
    return new ChromeDriver(options);
  }

  private FirefoxDriver firefox(SelenideProxyServer proxy) {
    WebDriverManager.firefoxdriver().setup();

    FirefoxOptions options = new FirefoxOptions();
    if (isHeadless()) options.setHeadless(true);
    options.setProxy(proxy.getSeleniumProxy());
    options.addPreference("network.proxy.no_proxies_on", "");
    options.addPreference("network.proxy.allow_hijacking_localhost", true);
    return new FirefoxDriver(options);
  }
}
