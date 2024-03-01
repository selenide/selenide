package integration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.WebdriverUnwrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

final class ReopenBrowserTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    closeWebDriver();
  }

  @Test
  void open_shouldReopenBrowser_if_itHasDiedMeanwhile() {
    open("about:blank");
    Configuration.reopenBrowserOnFail = true;
    WebDriver diedWebdriver = WebDriverRunner.getWebDriver();
    diedWebdriver.quit();

    open("about:blank");
    $("body").shouldHave(exactText(""));
    WebDriver newWebdriver = WebDriverRunner.getWebDriver();
    assertThat(newWebdriver).isNotEqualTo(diedWebdriver);
  }

  @Test
  void open_shouldNotReopenBrowser_if_disabled() {
    open("about:blank");
    Configuration.reopenBrowserOnFail = false;
    WebDriver diedWebdriver = WebDriverRunner.getWebDriver();
    diedWebdriver.quit();

    assertThatThrownBy(() -> open("about:blank")).isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("has been closed meanwhile")
      .hasMessageContaining("cannot create a new webdriver because reopenBrowserOnFail=false");
    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();
  }

  @Test
  void open_shouldOpenBrowser_evenIf_reopenBrowserOnFail_isFalse() {
    Configuration.reopenBrowserOnFail = false;

    open("about:blank");
    $("body").shouldHave(exactText(""));
  }

  @Test
  void open_new_browser_with_custom_config() {
    var url = "about:blank";
    open(url);
    var config = new SelenideConfig();
    config.browser("firefox");
    config.pageLoadStrategy("none");
    WebDriverRunner.replaceBrowser(config);
    var webDriver = WebDriverRunner.getWebDriver();
    var capabilities = WebdriverUnwrapper.unwrapRemoteWebDriver(webDriver).getCapabilities();
    assertThat(capabilities.getBrowserName()).isEqualTo(config.browser());
    assertThat(capabilities.getCapability("pageLoadStrategy")).isEqualTo(config.pageLoadStrategy());
    open(url);
    assertThat(webDriver.getCurrentUrl()).isEqualTo(url);
  }

  @Test
  void open_new_browser_with_custom_window_size() {
    var height = 800;
    var width = 544;
    var config = new SelenideConfig();
    config.browserSize("%sx%s".formatted(width, height));
    Selenide.open(config);
    var driver = WebDriverRunner.getWebDriver();
    var size = driver.manage().window().getSize();
    assertThat(size.height).isEqualTo(height);
    assertThat(size.width).isEqualTo(width);
  }

  @Test
  void open_new_browser_with_custom_config_and_open_relative_page() {
    var config = new SelenideConfig();
    config.pageLoadTimeout(10000);
    Selenide.open("/start_page.html", config);
    assertThat(Selenide.$("h1").text()).isEqualTo("Selenide");
    var secondsTimeout = WebDriverRunner.getWebDriver().manage().timeouts().getPageLoadTimeout().getSeconds();
    assertThat(secondsTimeout * 1000).isEqualTo(config.pageLoadTimeout());
  }
}
