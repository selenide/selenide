package integration;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.StaticConfig;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

public class CustomWebdriverWithSelenideProxyTest extends IntegrationTest {
  @BeforeEach
  @AfterEach
  void setUp() {
    closeWebDriver();
  }

  @Test
  public void userCanUserCustomWebdriverWithSelenideProxy() {
    toggleProxy(true);

    SelenideProxyServer proxy = new SelenideProxyServer(new StaticConfig(), null);
    proxy.start();
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setProxy(proxy.createSeleniumProxy());

    WebDriverRunner.setWebDriver(new ChromeDriver(chromeOptions), proxy);

    open("/basic-auth/hello", AuthenticationType.BASIC, new Credentials("scott", "tiger"));
    $("body").shouldHave(text("Hello, scott:tiger!"));
  }
}
