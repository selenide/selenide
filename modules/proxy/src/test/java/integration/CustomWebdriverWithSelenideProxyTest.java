package integration;

import com.codeborne.selenide.BasicAuthCredentials;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.AuthenticationType.BASIC;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
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
      WebDriver webDriver = isChrome() ? openChrome(proxy) : openFirefox(proxy);
      try {
        WebDriverRunner.setWebDriver(webDriver, proxy);

        open("/basic-auth/hello", BASIC, new BasicAuthCredentials(domain(), "scott", scottPassword()));
        $("body").shouldHave(partialText("Hello, scott:" + scottPassword()));
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
}
