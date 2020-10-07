package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class UnopenedBrowserTest extends IntegrationTest {
  @BeforeEach
  void givenNoOpenedBrowsers() {
    closeWebDriver();
  }

  @Test
  void openWoParamsCreatesNewDriver() {
    open();
    assertThat(WebDriverRunner.getWebDriver()).isNotNull();
  }

  @Test
  void dollarShouldNotOpenBrowser() {
    assertThatThrownBy(() ->
      $("div").shouldBe(visible)
    ).isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread")
      .hasMessageEndingWith("You need to call open(url) first.");
  }

  @Test
  void dollarsShouldNotOpenBrowser() {
    WebDriverManager webDriverManager = WebDriverManager.chromedriver().avoidBrowserDetection();
    webDriverManager.setup();

    assertThatThrownBy(() ->
      $$("div").shouldHave(size(666))
    ).isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread")
      .hasMessageEndingWith("You need to call open(url) first.");
  }

  @Test
  void canDeclareSelenideElements_beforeOpeningBrowsers() {
    SelenideElement header = $("h1");
    openFile("page_with_selects_without_jquery.html");
    header.shouldBe(visible).shouldHave(text("Page with selects"));
  }

  @Test
  void canDeclareElementsCollection_beforeOpeningBrowsers() {
    ElementsCollection headers = $$("h2");
    openFile("page_with_selects_without_jquery.html");
    headers
      .shouldHave(size(3))
      .shouldHave(texts("Dropdown list", "Options with 'apostrophes' and \"quotes\"", "Radio buttons"));
  }

  @Test
  void canDeclareSelenideElements_beforeOpeningBrowsers_evenIfUsingCustomWebdriver() {
    assumeTrue(browser().isChrome());
    useProxy(false);

    SelenideElement header = $("h1");
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    ChromeDriver driver = new ChromeDriver(addHeadless(addSslErrorIgnoreCapabilities(options)));
    try {
      WebDriverRunner.setWebDriver(driver);
      openFile("page_with_selects_without_jquery.html");
      header.shouldBe(visible).shouldHave(text("Page with selects"));
    }
    finally {
      WebDriverRunner.closeWebDriver();
      driver.quit();
    }
  }
}
