package integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.using;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static org.assertj.core.api.Assumptions.assumeThat;

class CustomWebdriverTest extends IntegrationTest {
  private WebDriver browser1;
  private WebDriver browser2;

  @BeforeAll
  static void setUpWebdrivers() {
    assumeThat(isChrome() || isFirefox()).isTrue();

    close();
    if (isFirefox()) WebDriverManager.firefoxdriver().setup();
    if (isChrome()) WebDriverManager.chromedriver().setup();
  }

  @BeforeEach
  void setUpTwoBrowsers() {
    useProxy(false);

    browser1 = isFirefox() ?
      new FirefoxDriver(addSslErrorIgnoreCapabilities(addHeadless(new FirefoxOptions()))) :
      new ChromeDriver(addSslErrorIgnoreCapabilities(addHeadless(new ChromeOptions())));
    browser2 = isFirefox() ?
      new FirefoxDriver(addSslErrorIgnoreCapabilities(addHeadless(new FirefoxOptions()))) :
      new ChromeDriver(addSslErrorIgnoreCapabilities(addHeadless(new ChromeOptions())));
  }

  @Test
  void userCanSwitchBetweenWebdrivers_using_setWebDriver() {
    setWebDriver(browser1);
    openFile("page_with_selects_without_jquery.html");
    $("h1").shouldBe(visible);

    setWebDriver(browser2);
    openFile("page_with_selects_without_jquery.html");
    $("h2").shouldBe(visible);

    setWebDriver(browser1);
    openFile("page_with_selects_without_jquery.html");
    $("h1").shouldBe(visible);
  }

  @Test
  void userCanSwitchBetweenWebdrivers_usingIn() {
    using(browser1, () -> {
      openFile("page_with_selects_without_jquery.html");
      $("h1").shouldBe(visible);
    });

    using(browser2, () -> {
      openFile("page_with_selects_without_jquery.html");
      $("h2").shouldBe(visible);
    });

    using(browser1, () -> {
      openFile("page_with_selects_without_jquery.html");
      $("h1").shouldBe(visible);
    });
  }

  @AfterEach
  void tearDown() {
    if (browser1 != null) browser1.quit();
    if (browser2 != null) browser2.quit();
  }
}
